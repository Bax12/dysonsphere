package de.bax.dysonsphere.tileentities;

import java.util.HashMap;
import java.util.Map;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class DSMonitorTile extends BaseTile {

    protected float dsCompletionPercentage = 0;
    protected double dsEnergy = 0;
    Map<Item, Integer> dsParts = new HashMap<>();
    protected int ticksElapsed = 0;

    protected double lastEnergy = 0;
    protected int lashPartHash = 0;

    public DSMonitorTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_MONITOR.get(), pos, state);
    }

    public void tick(){
        if(!level.isClientSide && ticksElapsed++ % 10 == 0){
            level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                dsParts = new HashMap<>(ds.getDysonSphereParts());
                dsEnergy = ds.getDysonSphereEnergy();
                dsCompletionPercentage = ds.getCompletionPercentage();
            });

            
            boolean needsUpdate = false;
            if(lastEnergy != dsEnergy){
                lastEnergy = dsEnergy;
                needsUpdate = true;
            }
            int hash = dsParts.hashCode();
            if(lashPartHash != hash){
                lashPartHash = hash;
                needsUpdate = true;
            }
            if(needsUpdate){
                sendSyncPackageToNearbyPlayers();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("completion", dsCompletionPercentage);
        tag.putDouble("energy", dsEnergy);
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
}
