package de.bax.dysonsphere.schematic.baseElements;

import java.util.List;

import de.bax.dysonsphere.schematic.ISchematicElement;
import net.minecraft.nbt.CompoundTag;

public class Output implements ISchematicElement {

    String name;
    boolean state;

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 5;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getInputs() {
        return List.of();
    }

    @Override
    public List<String> getOutputs() {
        return List.of(name);
    }

    @Override
    public void evaluate(int packedInputs) {
        state = (packedInputs & 1) == 1;
    }

    @Override
    public boolean getState(String output) {
        return state;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = ISchematicElement.super.serialize();
        tag.putString("name", name);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        ISchematicElement.super.deserialize(nbt);
        this.name = nbt.getString("name");
    }
    
}
