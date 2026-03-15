package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.entities.DeliveryDropEntity;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import de.bax.dysonsphere.recipes.CargoDeliveryRecipe;
import de.bax.dysonsphere.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CargoReceiverTile extends BaseTile implements IUpdateReceiverTile {

    public static int fluidCapacity = 10000;

    public static enum Status {
        READY, //no energy or no recipe
        WORKING, //energy + recipe
        BLOCKED_OUTPUT, //output full
        BLOCKED_SKY, //sky blocked
        BLOCKED_CONSTRUCTS //missing required constructs for recipe
    }

    public ItemStackHandler inventory = new ItemStackHandler(10){
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            shouldUpdate = true;
        };
    };

    public FluidTankCustom tank = new FluidTankCustom(fluidCapacity){
        
        protected void onContentsChanged() {
            super.onContentsChanged();
            shouldUpdate = true;
        };

        @Override
        public boolean canFill() {
            return false;
        };
    };

    public IDSEnergyReceiver dsReceiver = new IDSEnergyReceiver() {

        @Override
        public boolean canReceive() {
            return getLevel() != null && getLevel().canSeeSky(getBlockPos().above());
        }

        @Override
        public int getMaxReceive() {
            if((isWorking() || isReady()) && curRecipe != null){//should never be working or ready with null recipe. Better safe then sorry.
                return dsPowerDraw;
            }
            return 0;
        }

        @Override
        public void registerToDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.registerEnergyReceiver(lazyDSReceiver);
        }

        @Override
        public void removeFromDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.removeEnergyReceiver(lazyDSReceiver);
        }
        
    };

    protected LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> tank);
    protected LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);
    protected LazyOptional<IDSEnergyReceiver> lazyDSReceiver = LazyOptional.of(() -> dsReceiver);

    protected int ticksElapsed = 0;
    protected int energyStored;
    protected int dsPowerDraw;
    protected LazyOptional<IFluidHandler>[] fluidNeighbors = new LazyOptional[6];
    protected CargoDeliveryRecipe curRecipe;
    protected boolean shouldUpdate;
    protected Status status = Status.READY;

    protected ResourceLocation recipeName;
    protected int lastEnergy = 0;

    public CargoReceiverTile(BlockPos pos, BlockState state) {
        super(ModTiles.CARGO_RECEIVER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.FLUID_HANDLER)){
            return lazyFluidHandler.cast();
        } else if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return lazyInventory.cast();
        } else if(cap.equals(DSCapabilities.DS_ENERGY_RECEIVER)) {
            return lazyDSReceiver.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
        lazyInventory.invalidate();
        lazyDSReceiver.invalidate();
    }

    public void tick(){
        if(recipeName != null){
            curRecipe = (CargoDeliveryRecipe) level.getRecipeManager().byKey(recipeName).get();
            recipeName = null;
            shouldUpdate = true;
        }
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                splitShareFluid();
                if(level.canSeeSky(getBlockPos().above())){
                    if(curRecipe != null){
                        if(energyStored >= curRecipe.energy()){ //assume we generated the output last tick.
                            energyStored %= curRecipe.energy();
                        }
                        if(canOutput()){
                            energyStored += level.getCapability(DSCapabilities.DYSON_SPHERE).map((dysonsphere) -> {
                                var constructs = dysonsphere.getEnabledConstructs();
                                if(constructs.containsAll(curRecipe.requiredConstructs())){
                                    setStatus(Status.READY);
                                    return dsReceiver.getCurrentReceive(dysonsphere);
                                } else {
                                    setStatus(Status.BLOCKED_CONSTRUCTS);
                                }
                                return 0;
                            }).orElse(0);
                            if(energyStored >= curRecipe.energy()){
                                int overflow = energyStored / curRecipe.energy(); //at least 1, so no additional check needed
                                ItemStack stack = curRecipe.itemOutput().copy();
                                stack.setCount(stack.getCount() * overflow);
                                int i = 0;
                                while(!stack.isEmpty()){
                                    stack = inventory.insertItem(i, stack, false);
                                    i++;
                                    if(i > inventory.getSlots()) break;
                                }
                                FluidStack fluid = curRecipe.fluidOutput().copy();
                                fluid.setAmount(fluid.getAmount() * overflow);
                                tank.fillInternal(fluid, FluidAction.EXECUTE);
                                // energyStored = 0;
                                setStatus(Status.WORKING);//for request == recipe.energy() situations
                                shouldUpdate = true;

                                //
                                level.playSound(null, getBlockPos(), ModSounds.CARGO_DELIVERY.get(), SoundSource.BLOCKS, 1f, (this.level.random.nextFloat() * 0.2f) + 0.8f);
                                DeliveryDropEntity strike = new DeliveryDropEntity(level);
                                strike.setTargetY(this.getBlockPos().getY()).setPos(this.getBlockPos().getX() + 0.5d, this.getBlockPos().getY() + 200d, this.getBlockPos().getZ() + 0.5d);
                                
                                level.addFreshEntity(strike);
                            }
                            if(lastEnergy != energyStored){
                                setStatus(Status.WORKING);
                                shouldUpdate = true;
                            }
                        } else {
                            setStatus(Status.BLOCKED_OUTPUT);
                        }
                    } else {
                        setStatus(Status.READY);
                    }
                } else {
                    setStatus(Status.BLOCKED_SKY);
                }
                
                

                if(shouldUpdate){
                    this.setChanged();
                    sendSyncPackageToNearbyPlayers();
                    shouldUpdate = false;
                    lastEnergy = energyStored;
                }
            }
        } else {
            if(curRecipe != null && energyStored >= curRecipe.energy()){
                for (int i = 10; i > 0; i--){
                        double x = level.random.nextDouble() - 0.5d;
                        double z = level.random.nextDouble() - 0.5d;
                        level.addParticle(ParticleTypes.CLOUD, (double)getBlockPos().getX() + x + 0.5d, (double)getBlockPos().getY() + 0.2d, (double)getBlockPos().getZ() + z + 0.5d, x, -0.25d, z);
                }
            }
        }
    }

    protected void setStatus(Status status){
        if(this.status != status){
            this.status = status;
            shouldUpdate = true;
        }
    }

    public boolean isWorking(){
        return status == Status.WORKING;
    }

    public boolean isReady(){
        return status  == Status.READY;
    }

    public Status getStatus() {
        return status;
    }

    protected boolean canOutput(){
        if(curRecipe == null) return false;
        ItemStack stack = curRecipe.itemOutput().copy();
        int i = 0;
        while(!stack.isEmpty()){
            stack = inventory.insertItem(i, stack, true);
            i++;
            if(i > inventory.getSlots()) return false;
        }

        FluidStack fluid = curRecipe.fluidOutput();
        int filled = tank.fillInternal(fluid, FluidAction.SIMULATE);
        if(filled < fluid.getAmount()) return false;

        return true;
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("recipe")){
            recipeName = new ResourceLocation(pTag.getString("recipe")); //level is null on first load. This a workaround
        }
        energyStored = pTag.getInt("energy");
        dsPowerDraw = pTag.getInt("dsPowerDraw");
        tank.readFromNBT(pTag.getCompound("tank"));
        inventory.deserializeNBT(pTag.getCompound("inv"));
        status = Status.values()[pTag.getInt("status")];
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if(curRecipe != null){
            pTag.putString("recipe", curRecipe.id().toString());
        }
        pTag.putInt("energy", energyStored);
        pTag.putInt("dsPowerDraw", dsPowerDraw);
        pTag.put("tank", tank.writeToNBT(new CompoundTag()));
        pTag.put("inv", inventory.serializeNBT());
        pTag.putInt("status", status.ordinal());
    }
    
    public void onNeighborChange() {
        updateNeighbors();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
            dsReceiver.registerToDysonSphere(ds);
        });
        updateNeighbors();
        ticksElapsed = level.getRandom().nextInt(4); //set ticksElapsed to 0-4 on load to not calculate all on the same server tick. Probably useless

    }


    protected void updateNeighbors(){
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = (level.getBlockEntity(getBlockPos().relative(dir)));
            if(neighbor != null){
                LazyOptional<IFluidHandler> neighborHandler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    fluidNeighbors[dir.ordinal()] = neighborHandler;
                }
            }
        }
    }

    protected void splitShareFluid(){
        for(LazyOptional<IFluidHandler> neighbor : fluidNeighbors){
            if(neighbor != null && neighbor.isPresent()){
                neighbor.ifPresent((neiFluid) -> {
                    int transfer = neiFluid.fill(tank.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE);
                    if(transfer > 0){
                        neiFluid.fill(tank.drain(transfer, FluidAction.EXECUTE), FluidAction.EXECUTE);
                    }
                });
            }
        }
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    public int getDsPowerDraw() {
        return dsPowerDraw;
    }

    public void setDsPowerDraw(int dsPowerDraw) {
        this.dsPowerDraw = dsPowerDraw;
    }

    public void setCurrentRecipe(CargoDeliveryRecipe curRecipe) {
        this.curRecipe = curRecipe;
    }

    public CargoDeliveryRecipe getCurrentRecipe() {
        return curRecipe;
    }

    public int getProgressScaled(int scale){
        if(curRecipe == null){
            return 0;
        }
        if(energyStored >= curRecipe.energy()) {
            return scale;
        }
        return ((isWorking() && energyStored == 0) ? scale : (scale * energyStored / curRecipe.energy()));
    }

    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        setDsPowerDraw(updateTag.getInt("target"));
        if(updateTag.contains("recipe")){
            setCurrentRecipe((CargoDeliveryRecipe) level.getRecipeManager().byKey(new ResourceLocation(updateTag.getString("recipe"))).get());
        } else {
            setCurrentRecipe(null);
        }
    }

    @Override
    public void sendGuiUpdate() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("target", dsPowerDraw);
        if(curRecipe != null){
            tag.putString("recipe", curRecipe.id().toString());
        }
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

    public int getEnergyStored() {
        return energyStored;
    }
}
