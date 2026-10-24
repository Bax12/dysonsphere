package de.bax.dysonsphere.schematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.recipes.SchematicBaseInputRecipe;
import de.bax.dysonsphere.recipes.SchematicResultRecipe;
import de.bax.dysonsphere.schematic.baseElements.Input;
import de.bax.dysonsphere.schematic.baseElements.Output;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class Schematic implements ISchematicElement {
    
    int height, width;
    List<ElementPart> parts;
    String name;
    List<Input> inputs;
    List<Output> outputs;
    List<Connection> connections; //replaced by map?
    short[] truthTable;
    boolean isValid = false;
    Map<ElementPart, List<Connection>> incomingConnections;


    public class ElementPart {
        ISchematicElement element;
        int x, y;

        ElementPart(ISchematicElement element, int x, int y){
            this.element = element;
            this.x = x;
            this.y = y;
        }
    }

    public class Connection {

        ElementPart source;
        ElementPart target;
        String sourceOutput;
        String targetInput;

        Connection(ElementPart source, ElementPart target, String sourceOutput, String targetInput){
            this.source = source;
            this.target = target;
            this.sourceOutput = sourceOutput;
            this.targetInput = targetInput;
        }

        ISchematicElement getSourceElement(){
            return source.element;
        }

        ISchematicElement getTargetElement(){
            return target.element;
        }
    }

    public Schematic(){
        parts = new ArrayList<>();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        truthTable = null;
    }



    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getInputs() {
        return parts.stream().filter((part) -> part.element instanceof Input).map((part) -> part.element.getName()).toList();
    }

    @Override
    public List<String> getOutputs() {
        return parts.stream().filter((part) -> part.element instanceof Output).map((part) -> part.element.getName()).toList();
    }

    @Override
    public boolean getState(String output) {
        return parts.stream().filter((part) -> part.element instanceof Output && part.element.getName().equals(output)).map((part) -> part.element.getState(output)).findFirst().orElseGet(() -> false);
    }

    @Override
    public void evaluate(int packedInputs) {
        packedInputs &= ~(0xFFFFFFFF << inputs.size()); //ignore all inputs over input count, there probably is a cleaner way to do this
        if(!isValid){
            calcTruthTable();
        }
        int packedOutputs = truthTable[packedInputs];
        for(int i = 0; i < outputs.size(); i++){
            outputs.get(i).evaluate(packedOutputs << i);
        }
    }

    public void deserialize(CompoundTag nbt){
        width = nbt.getInt("width");
        height = nbt.getInt("height");
        name = nbt.getString("name");

        parts.clear();
        inputs.clear();
        outputs.clear();
        connections.clear();
        CompoundTag tag = nbt.getCompound("parts");

        int i = tag.getInt("partCount");
        for(; i > 0; i--){
            CompoundTag partTag = tag.getCompound("" + i);
            int x = partTag.getInt("x");
            int y = partTag.getInt("y");
            ISchematicElement element = ISchematicElement.load(partTag.getCompound("element"));
            ElementPart entry = new ElementPart(element, x, y);
            parts.add(entry);
            if(entry.element instanceof Input input){
                inputs.add(input);
            } else if(entry.element instanceof Output output){
                outputs.add(output);
            }
        }

        loadConnections(tag.getCompound("connections"));
        isValid = false;
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("width", width);
        tag.putInt("height", height);
        tag.putString("name", name);
        
        CompoundTag partsTag = new CompoundTag();
        partsTag.putInt("partCount", parts.size());
        int i = 0;
        for(ElementPart entry : parts){
            CompoundTag partTag = new CompoundTag();
            partTag.putInt("x", entry.x);
            partTag.putInt("y", entry.y);
            partTag.put("element", entry.element.serialize());
            partsTag.put("" + i++, partTag);
        }

        tag.put("parts", partsTag);

        tag.put("connections", saveConnections());

        return tag;
    }

    protected void loadConnections(CompoundTag tag){
        for(String key : tag.getAllKeys()) {
            CompoundTag conTag = tag.getCompound(key);
            addConnection(parts.get(conTag.getInt("sourceId")), parts.get(conTag.getInt("targetId")), conTag.getString("sourceName"), conTag.getString("targetName"));
        }
    }

    protected CompoundTag saveConnections(){
        CompoundTag tag = new CompoundTag();
        
        int i = 0;
        for(Connection connection : connections){
            CompoundTag conTag = new CompoundTag();

            conTag.putInt("sourceId", parts.indexOf(connection.source));
            conTag.putInt("targetId", parts.indexOf(connection.target));

            conTag.putString("sourceName", connection.sourceOutput);
            conTag.putString("targetName", connection.targetInput);

            tag.put("" + i++, conTag);
        }


        return tag;
    }

    public void addPart(ISchematicElement element, int y, int x){
        parts.add(new ElementPart(element, x, y));
        if(element instanceof Input input){
            inputs.add(input);
        }
        if(element instanceof Output output){
            outputs.add(output);
        }
        isValid = false;
    }

    public void removePart(ElementPart entry){
        parts.remove(entry);
        if(entry.element instanceof Input input){
            inputs.remove(input);
        }
        if(entry.element instanceof Output output){
            outputs.remove(output);
        }
        connections.removeIf((connection) -> connection.source.equals(entry) || connection.target.equals(entry));
        isValid = false;
    }

    public void addConnection(ElementPart source, ElementPart target, String sourceOutput, String targetInput){
        connections.add(new Connection(source, target, sourceOutput, targetInput));
        isValid = false;
    }

    public void removeConnection(Connection connection){
        connections.remove(connection);
        isValid = false;
    }



    public static Schematic load(CompoundTag tag){
        Schematic schematic = new Schematic();
        schematic.deserialize(tag);
        return schematic;
    }

    public void calcTruthTable(){
        int inputStateCount = 1 << inputs.size();
        truthTable = new short[inputStateCount];
        for(int i = 0; i < (inputStateCount); i++){
            evaluateParts(i);
            short packedOutput = 0;
            for(int j = 0; j < outputs.size(); j++){
                packedOutput |= (outputs.get(j).getState("") ? 1 : 0) << j; //there should be no difference between += and |= here.
            }
            truthTable[i] = packedOutput;
        }
        isValid = true;
    }

    protected void evaluateParts(int packedInput){
        boolean evaluated[] = new boolean[parts.size()];
        for(int i = 0; i < outputs.size(); i++){
            int packedPartInput = 0;
            for(Connection connection : incomingConnections.get(outputs.get(i))){
                //TODO - maybe rework entirly
                //if connection.input not is evaluated -> evaluate
                //set bit of packedPartInput based on state of connection.input 
            }

            outputs.get(i).evaluate(packedPartInput);
        }
    }


    public static Optional<ItemStack> getResultFromSchematic(Level level, Schematic schematic){
        if(!schematic.isValid){
            schematic.calcTruthTable();
        }
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.SCHEMATIC_RESULT_TYPE.get()).stream().filter((recipe) -> recipe.matches(schematic.truthTable, level)).findFirst().map((recipe) -> {
            return recipe.getResultItem(level.registryAccess()).copy();
        });
    }

    public static List<Ingredient> getInputsFromSchematic(Level level, Schematic schematic){
        List<Ingredient> inputs = new ArrayList<>();
        List<SchematicBaseInputRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.SCHEMATIC_INPUT_TYPE.get());
        for(ElementPart part : schematic.parts){
            SchematicBaseInputRecipe recipe = recipes.stream().filter((rec) -> rec.matches(part.element)).findFirst().orElse(null);
            if(recipe != null){
                inputs.add(recipe.input());
            } else if(part.element instanceof Schematic schematicPart) {
                inputs.addAll(getInputsFromSchematic(level, schematicPart));
            }
        }

        return inputs;
    }

}
