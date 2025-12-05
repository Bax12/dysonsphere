package de.bax.dysonsphere.tileentities;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class DSMonitorTile extends BaseTile {

    protected float dsCompletionPercentage = 0;
    protected double dsEnergy = 0;
    protected Map<Item, Long> dsParts = new HashMap<>();
    protected float dsUsage = 0;
    protected double dsEnergyDraw = 0;
    protected int ticksElapsed = 0;

    protected double lastEnergy = 0;
    protected int lastPartHash = 0;
    protected float lastUsage = 0;
    protected double lastEnergyDraw = 0;

    protected boolean dirty = false;

    public DSMonitorTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_MONITOR.get(), pos, state);
    }

    public DSMonitorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(){
        if(!level.isClientSide && ticksElapsed++ % 10 == 0){
            if(level.getCapability(DSCapabilities.DYSON_SPHERE).isPresent()){
                level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                    dsParts = new HashMap<>(ds.getDysonSphereParts());
                    dsEnergy = ds.getDysonSphereEnergy();
                    dsCompletionPercentage = ds.getCompletionPercentage();
                    dsUsage = ds.getUtilization();
                    dsEnergyDraw = ds.getEnergyRequested();
                });
            } else {
                dsParts.clear();
                dsEnergy = -1;
                dsCompletionPercentage = -1;
                dsUsage = -1;
                dsEnergyDraw = -1;
            }
            

            if(dsCompletionPercentage > 0){
                for(Player player : level.getNearbyPlayers(TargetingConditions.forNonCombat().ignoreInvisibilityTesting(), null, AABB.ofSize(worldPosition.getCenter(), 5d, 5d, 5d))){
                    ModAdvancements.DS_PROGRESS_TRIGGER.trigger((ServerPlayer) player, dsCompletionPercentage);
                }
            }
            
            // boolean needsUpdate = false;
            // if(lastEnergy != dsEnergy){
            //     lastEnergy = dsEnergy;
            //     needsUpdate = true;
            // }
            // int hash = dsParts.hashCode();
            // if(lashPartHash != hash){
            //     lashPartHash = hash;
            //     needsUpdate = true;
            // }
            // if(needsUpdate){
            //     sendSyncPackageToNearbyPlayers();
            // }
            int hash = dsParts.hashCode();
            if(lastEnergy != dsEnergy || lastPartHash != hash || lastUsage != dsUsage || lastEnergyDraw != dsEnergyDraw){
                lastEnergy = dsEnergy;
                lastPartHash = hash;
                lastUsage = dsUsage;
                lastEnergyDraw = dsEnergyDraw;
                dirty = true;
            }
            if(dirty){ //to enable sync trigger in child classes
                sendSyncPackageToNearbyPlayers();
                dirty = false;
            }
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("completion", dsCompletionPercentage);
        tag.putDouble("energy", dsEnergy);
        tag.putFloat("usage", dsUsage);
        tag.putDouble("energy_draw", dsEnergyDraw);
        CompoundTag invTag = new CompoundTag();
        dsParts.forEach((item, count) -> {
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
            if(itemKey != null){
                invTag.putLong(itemKey.toString(), count);
            }
        });
        if(invTag.size() > 0){
            tag.put("parts", invTag);
        }
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        dsCompletionPercentage = tag.getFloat("completion");
        dsEnergy = tag.getDouble("energy");
        dsUsage = tag.getFloat("usage");
        dsEnergyDraw = tag.getDouble("energy_draw");
        CompoundTag inv = tag.getCompound("parts");
            if(inv != null){
                dsParts.clear(); //without it causes issues when removing the last parts of the dysonsphere
                for(String itemKey : inv.getAllKeys()){
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
                    long count = inv.getLong(itemKey);
                    dsParts.put(item, count);
                }
            }
    }

    public Map<Item, Long> getDsParts() {
        return dsParts;
    }

    public double getDsEnergy() {
        return dsEnergy;
    }
    
    public float getDsCompletionPercentage() {
        return dsCompletionPercentage;
    }

    public float getDsUsage() {
        return dsUsage;
    }

    public double getDsEnergyDraw() {
        return dsEnergyDraw;
    }
}
