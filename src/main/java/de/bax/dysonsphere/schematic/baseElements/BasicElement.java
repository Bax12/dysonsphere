package de.bax.dysonsphere.schematic.baseElements;

import java.util.List;

import de.bax.dysonsphere.schematic.ISchematicElement;

public abstract class BasicElement implements ISchematicElement {
    
    boolean state;

    @Override
    public int getWidth() {
        return 15;
    }

    @Override
    public int getHeight() {
        return 5;
    }

    @Override
    public List<String> getInputs() {
        return List.of("A", "B");
    }

    @Override
    public List<String> getOutputs() {
        return List.of("O");
    }

    @Override
    public boolean getState(String output) {
        return state;
    }

    public static class And extends BasicElement {

        @Override
        public String getName() {
            return "AND";
        }

        @Override
        public void evaluate(int packedInputs) {
            state = (packedInputs & 0x01) == 0x01 && (packedInputs & 0x10) == 0x10;
        }

    }

    public static class Or extends BasicElement {

        @Override
        public String getName() {
            return "OR";
        }

        @Override
        public void evaluate(int packedInputs) {
            state = (packedInputs & 0x01) == 0x01 || (packedInputs & 0x10) == 0x10;
        }

    }
    
    public static class Nand extends BasicElement {

        @Override
        public String getName() {
            return "NAND";
        }

        @Override
        public void evaluate(int packedInputs) {
            state = !((packedInputs & 0x01) == 0x01 && (packedInputs & 0x10) == 0x10);
        }

    }

    public static class Nor extends BasicElement {

        @Override
        public String getName() {
            return "NOR";
        }

        @Override
        public void evaluate(int packedInputs) {
            state = !((packedInputs & 0x01) == 0x01 || (packedInputs & 0x10) == 0x10);
        }

    }

    public static class Not extends BasicElement {

        @Override
        public String getName() {
            return "NOT";
        }

        @Override
        public List<String> getInputs() {
            return List.of("I");
        }

        @Override
        public void evaluate(int packedInputs) {
            state = (packedInputs & 0x1) == 0;
        }

    }




}
