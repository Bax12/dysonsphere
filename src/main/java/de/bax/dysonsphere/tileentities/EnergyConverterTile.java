package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.blocks.OreSpireBlock;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.AcceptorEnergyWrapper;
import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider;
import de.bax.dysonsphere.capabilities.inputHatch.InputAcceptorHandler;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

public class EnergyConverterTile extends BaseTile implements ITintableTile{

    public static int ENERGY_CAPACITY = 200_000;
    public static int CONVERSION_RATE = 10_000;

    public EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY){
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.receiveEnergy(maxReceive, simulate);
        };
        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.extractEnergy(maxExtract, simulate);
        };
    };

    public InputAcceptorHandler acceptorHandler = new InputAcceptorHandler(this){
        public void addInputProvider(LazyOptional<IInputProvider> provider) {
            super.addInputProvider(provider);
            if(provider.isPresent()){
                setChanged();
            }
        };

        public void refreshProvider() {
            super.refreshProvider();
            setChanged();
        };
    };

    protected LazyOptional<IItemHandler> output = LazyOptional.empty();
    protected ItemStack drop = ItemStack.EMPTY;

    protected LazyOptional<IEnergyStorage> lazyEnergy = LazyOptional.of(() -> energyStorage);
    protected LazyOptional<IInputAcceptor> lazyAcceptor = LazyOptional.of(() -> acceptorHandler);

    public AcceptorEnergyWrapper acceptorStorage = new AcceptorEnergyWrapper(lazyEnergy, acceptorHandler);

    protected boolean dirty = false;
    protected int ticksElapsed = 0;
    protected BlockPos above;
    protected BlockPos below;
    protected boolean canWork;
    protected boolean lastWork = false;

    public EnergyConverterTile(BlockPos pos, BlockState state) {
        super(ModTiles.ENERGY_CONVERTER.get(), pos, state);
        
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ENERGY)){
            return lazyEnergy.cast();
        }
        if(cap.equals(DSCapabilities.INPUT_ACCEPTOR)){
            return lazyAcceptor.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergy.invalidate();
        lazyAcceptor.invalidate();
    }

    public void tick(){
        if(!level.isClientSide()){
            if(acceptorStorage.getEnergyStored() >= CONVERSION_RATE && !drop.isEmpty()){
                if(!output.isPresent()){
                    if(level.getBlockState(above).isAir()){
                        level.setBlock(above, ModBlocks.ORE_SPIRE_BLOCK.get().defaultBlockState().setValue(OreSpireBlock.HAS_BASE, true), 3);
                    }
                } else {
                    int amount = acceptorStorage.extractEnergy(Integer.MAX_VALUE, true) / CONVERSION_RATE;
                    ItemStack toCreate = drop.copyWithCount(amount);
                    int created = output.map((out) -> {
                        ItemStack toInsert = toCreate;
                        for(int i = 0; i < out.getSlots(); i++){
                            toInsert = out.insertItem(i, toInsert, false);
                            if(toInsert.isEmpty()) break;
                        }
                        return amount - toInsert.getCount();
                    }).orElse(0);
                    canWork = created > 0;
                    acceptorStorage.extractEnergy(created * CONVERSION_RATE, false);
                }
                
            } else {
                canWork = !drop.isEmpty() && output.isPresent();
            }
            
            
            
            if(ticksElapsed++ % 5 == 0 && dirty){
                dirty = false;
                sendSyncPackageToNearbyPlayers();
            }
        } else {
            if(lastWork != canWork){
                level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0);
                lastWork = canWork;
            }            
        }
    }

    public void onRemove(){
        acceptorHandler.onRemove();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.putBoolean("canWork", canWork);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        energyStorage.deserializeNBT(pTag.get("energy"));
        canWork = pTag.getBoolean("canWork");
    }

    public void onNeighborChange(){
        if(!level.isClientSide()){
            BlockEntity aboveBlock = level.getBlockEntity(above);
            if(aboveBlock != null){
                output = aboveBlock.getCapability(ForgeCapabilities.ITEM_HANDLER);
            } else {
                output = LazyOptional.empty();
            }
            drop = level.getBlockState(below).getDrops(new LootParams.Builder((ServerLevel) level).withParameter(LootContextParams.ORIGIN, getBlockPos().getCenter()).withParameter(LootContextParams.TOOL, Items.NETHERITE_PICKAXE.getDefaultInstance())).stream().filter((item) -> {
                return item.is(Tags.Items.RAW_MATERIALS);
            }).findFirst().orElse(ItemStack.EMPTY);
        }
        
    }

    @Override
    public void onLoad() {
        super.onLoad();
        below = getBlockPos().below(); //we ignore push events, everything else should reload the te, right?
        above = getBlockPos().above();
        onNeighborChange();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        dirty = true;
    }
    
    @Override
    public int getTintColor(int tintIndex) {
        return canWork ? 0xFFFFFF00 : 0xFFEE1111;
    }
}
