package de.bax.dysonsphere.capabilities.orbitalLaser;

import net.minecraft.world.item.ItemStack;

public class ItemLaserPatternContainer implements IOrbitalLaserPatternContainer {

    public static final String PATTERN_TAG = "pattern";
    protected ItemStack container;

    public ItemLaserPatternContainer(ItemStack container){
        this.container = container;
    }

    @Override
    public void setPattern(OrbitalLaserAttackPattern pattern) {
        if(pattern.isEmpty()){
            container.getOrCreateTag().remove(PATTERN_TAG);
        } else {
            container.getOrCreateTag().put(PATTERN_TAG, pattern.serializeNBT());
        }
        
    }

    @Override
    public OrbitalLaserAttackPattern getPattern() {
        if(!container.hasTag() || !container.getTag().contains(PATTERN_TAG)){
            return OrbitalLaserAttackPattern.EMPTY;
        }
        OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
        pattern.deserializeNBT(container.getTagElement(PATTERN_TAG));
        return pattern;
    }

    @Override
    public boolean isEmpty() {
        return !container.hasTag() || !container.getTag().contains(PATTERN_TAG) || getPattern().isEmpty();
    }
    
    

}
