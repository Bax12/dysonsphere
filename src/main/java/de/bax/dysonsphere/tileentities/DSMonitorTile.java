package de.bax.dysonsphere.tileentities;

import java.util.HashMap;
import java.util.Map;

import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class DSMonitorTile extends BaseTile {

    protected float dsCompletionPercentage = 0;
    protected double dsEnergy = 0;
    protected Map<Item, Integer> dsParts = new HashMap<>();
    protected float dsUsage = 0;
    protected double dsEnergyDraw = 0;
    protected int ticksElapsed = 0;

    protected double lastEnergy = 0;
    protected int lastPartHash = 0;
    protected float lastUsage = 0;
    protected double lastEnergyDraw = 0;

    public DSMonitorTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_MONITOR.get(), pos, state);
    }

    public void tick(){
        if(!level.isClientSide && ticksElapsed++ % 10 == 0){
            level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                dsParts = new HashMap<>(ds.getDysonSphereParts());
                dsEnergy = ds.getDysonSphereEnergy();
                dsCompletionPercentage = ds.getCompletionPercentage();
                dsUsage = ds.getUtilization();
                dsEnergyDraw = ds.getEnergyRequested();
            });

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
                sendSyncPackageToNearbyPlayers();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("completion", dsCompletionPercentage);
        tag.putDouble("energy", dsEnergy);
        tag.putFloat("usage", dsUsage);
        tag.putDouble("energy_draw", dsEnergyDraw);
        CompoundTag invTag = new CompoundTag();
        dsParts.forEach((item, count) -> {
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
            if(itemKey != null){
                invTag.putInt(itemKey.toString(), count);
            }
        });
        if(invTag.size() > 0){
            tag.put("parts", invTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        dsCompletionPercentage = tag.getFloat("completion");
        dsEnergy = tag.getDouble("energy");
        dsUsage = tag.getFloat("usage");
        dsEnergyDraw = tag.getDouble("energy_draw");
        CompoundTag inv = tag.getCompound("parts");
            if(inv != null){
                for(String itemKey : inv.getAllKeys()){
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
                    int count = inv.getInt(itemKey);
                    dsParts.put(item, count);
                }
            }
    }

    public Map<Item, Integer> getDsParts() {
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
