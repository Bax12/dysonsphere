package de.bax.dysonsphere.tileentities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class DSControllerTile extends DSMonitorTile implements IUpdateReceiverTile{

    public static int ENERGY_CAPACITY = 50000;
    public static int COMMAND_ENERGY = 1000;

    public EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY) {
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!simulate) {
                dirty = true;
            }
            return super.receiveEnergy(maxReceive, simulate);
        };

        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!simulate) {
                dirty = true;
            }
            return super.extractEnergy(maxExtract, simulate);
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    protected int enabledHash = 0;
    protected int disabledHash = 0;

    protected Set<Construct> enabledConstructs = new HashSet<>();
    protected Set<Construct> disabledConstructs = new HashSet<>();

    public DSControllerTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_CONTROLLER.get(), pos, state);

    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ENERGY)) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    public void tick() {
        super.tick();
        if (!level.isClientSide && ticksElapsed % 10 == 0) { // ticksElapsed++ in super.tick()
            if (level.getCapability(DSCapabilities.DYSON_SPHERE).isPresent()) {
                level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                    enabledConstructs = ds.getEnabledConstructs();
                    disabledConstructs = ds.getDisabledConstructs();
                });
            } else {
                enabledConstructs = ImmutableSet.of();
                disabledConstructs = ImmutableSet.of();
            }

            int newEnabledHash = enabledConstructs.hashCode();
            int newDisabledHash = disabledConstructs.hashCode();
            if (newEnabledHash != enabledHash || newDisabledHash != disabledHash) {
                dirty = true;
                enabledHash = newEnabledHash;
                disabledHash = newDisabledHash;
            }
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag enabledTags = new ListTag();
        enabledConstructs.forEach((construct) -> {
            enabledTags.add(construct.save());
        });
        tag.put("enabledConstructs", enabledTags);
        ListTag disabledTags = new ListTag();
        disabledConstructs.forEach((construct) -> {
            disabledTags.add(construct.save());
        });
        tag.put("disabledConstructs", disabledTags);
        tag.put("energy", energyStorage.serializeNBT());
    }

    @SuppressWarnings("null")
    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("enabledConstructs")) {
            enabledConstructs = ((ListTag) tag.get("enabledConstructs")).stream().map((con) -> {
                return Construct.load((CompoundTag) con);
            }).collect(ImmutableSet.toImmutableSet());
        }
        if (tag.contains("disabledConstructs")) {
            disabledConstructs = ((ListTag) tag.get("disabledConstructs")).stream().map((con) -> {
                return Construct.load((CompoundTag) con);
            }).collect(ImmutableSet.toImmutableSet());
        }
        if(tag.contains("energy")){
            energyStorage.deserializeNBT(tag.get("energy"));
        }
    }

    public Set<Construct> getEnabledConstructs(){
        return ImmutableSet.copyOf(enabledConstructs);
    }

    public Set<Construct> getDisabledConstructs(){
        return ImmutableSet.copyOf(disabledConstructs);
    }

    public void setConstructs(@Nonnull Set<Construct> enabledConstructs, @Nonnull Set<Construct> disabledConstructs){
        this.enabledConstructs = ImmutableSet.copyOf(enabledConstructs);
        this.disabledConstructs = ImmutableSet.copyOf(disabledConstructs);
    }

    @SuppressWarnings("null")
    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        if(this.energyStorage.extractEnergy(COMMAND_ENERGY, false) == COMMAND_ENERGY){
            this.energyStorage.extractEnergy(COMMAND_ENERGY, true);
            List<Construct> constructs = new ArrayList<Construct>(ModConstructs.registry().getValues());
            level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((dysonsphere) -> {
            if (updateTag.contains("enabledConstructs")) {
                ((ListTag) updateTag.get("enabledConstructs")).stream().map((con) -> {
                    return Construct.load((CompoundTag) con);
                }).forEach((construct) -> {
                    constructs.remove(construct);
                    if(!dysonsphere.addConstruct(construct, true)){
                        dysonsphere.enableConstruct(construct);
                    }
                });
                
            }
            if (updateTag.contains("disabledConstructs")) {
                ((ListTag) updateTag.get("disabledConstructs")).stream().map((con) -> {
                    return Construct.load((CompoundTag) con);
                }).forEach((construct) -> {
                    constructs.remove(construct);
                    if(!dysonsphere.addConstruct(construct, false)){
                        dysonsphere.disableConstruct(construct);
                    }
                });
            }
            if(!constructs.isEmpty()){ //every not send construct was removed, therefor will be removed from the dysonsphere
                constructs.forEach((con) -> {
                    dysonsphere.removeConstruct(con);
                });
            }
        });
        }
        
    }

    @Override
    public void sendGuiUpdate() {
        CompoundTag tag = new CompoundTag();
        ListTag enabledTags = new ListTag();
        enabledConstructs.forEach((construct) -> {
            enabledTags.add(construct.save());
        });
        tag.put("enabledConstructs", enabledTags);
        ListTag disabledTags = new ListTag();
        disabledConstructs.forEach((construct) -> {
            disabledTags.add(construct.save());
        });
        tag.put("disabledConstructs", disabledTags);
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

}
