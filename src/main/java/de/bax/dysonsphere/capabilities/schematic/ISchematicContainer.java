package de.bax.dysonsphere.capabilities.schematic;

import de.bax.dysonsphere.schematic.ISchematicElement;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ISchematicContainer {  

    public ISchematicElement getSchematic();

    public void setSchematic(ISchematicElement schematic);

}
