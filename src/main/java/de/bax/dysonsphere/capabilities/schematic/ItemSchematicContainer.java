package de.bax.dysonsphere.capabilities.schematic;

import de.bax.dysonsphere.schematic.ISchematicElement;
import net.minecraft.world.item.ItemStack;

public class ItemSchematicContainer implements ISchematicContainer {

    public static final String SCHEMATIC_TAG = "schematic";
    protected ItemStack container;

    public ItemSchematicContainer(ItemStack container){
        this.container = container;
    }

    @Override
    public ISchematicElement getSchematic() {
        if(!container.hasTag() || container.getTag().contains(SCHEMATIC_TAG)){
            return ISchematicElement.EMPTY;
        }
        return ISchematicElement.load(container.getOrCreateTag().getCompound(SCHEMATIC_TAG));
    }

    @Override
    public void setSchematic(ISchematicElement schematic) {
        if(schematic == null || schematic.equals(ISchematicElement.EMPTY)){
            container.getOrCreateTag().remove(SCHEMATIC_TAG);
        } else {
            container.getOrCreateTag().put(SCHEMATIC_TAG, schematic.serialize());
        }
        
    }
    
}
