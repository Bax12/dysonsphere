package de.bax.dysonsphere.recipes;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

//randomize truthTable based on level seed and defined complexity criteria - cached as SchematicResultData
//using bytes for the truth table and input/outputs the maximum inputs is limited to 16 instead of 32 and the maximum memory
//footprint of the tt is at 1MB instead of 128GB. As the count will be 16 at most, bytes are enough for the record values.
public record SchematicResultRecipe(ResourceLocation id, byte outputCount, byte inputCount, byte minDontCare, byte maxDontCare, ItemStack output) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    public boolean matches(short[] truthTable, Level level){
        int[] tt = getTruthTable(level);
        if (tt.length != truthTable.length) return false;
        for(int i = 0; i < tt.length; i++){
            int mask = ~(tt[i] >>> 16) & 0xFFFF;//extract dont-care mask from combined truth table
            if((tt[i] & mask) != (truthTable[i] & mask)) return false;
        }
        return true;
    }

    public int[] getTruthTable(Level level){
        return SchematicResultData.get((ServerLevel) level).getOrCreate(this, (ServerLevel) level);
    }

    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper pContainer, @Nonnull RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistryAccess) {
        return output();
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCHEMATIC_RESULT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SCHEMATIC_RESULT_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SchematicResultRecipe> {

        @Override
        public SchematicResultRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            //json should define the criteria, not the values
            
            ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), true);;
            byte outputCount = pSerializedRecipe.get("outputCount").getAsByte();
            byte inputCount = pSerializedRecipe.get("inputCount").getAsByte();
            byte minDontCare = pSerializedRecipe.get("minDontCare").getAsByte();
            byte maxDontCare = pSerializedRecipe.get("maxDontCare").getAsByte();


            return new SchematicResultRecipe(pRecipeId, outputCount, inputCount, minDontCare, maxDontCare, output);
        }

        @Override
        public @Nullable SchematicResultRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            //network data needs the values to sync, not the criteria
            // ItemStack output = pBuffer.readItem();
            // int count = pBuffer.readInt();
            // Integer[] truthTable = new Integer[count];
            // for (int i = 0; i < truthTable.length; i++) {
            //     truthTable[i] = pBuffer.readInt();
            // }
            // return new SchematicResultRecipe(pRecipeId, truthTable, output);

            ItemStack output = pBuffer.readItem();
            byte outputCount = pBuffer.readByte();
            byte inputCount = pBuffer.readByte();
            byte minDontCare = pBuffer.readByte();
            byte maxDontCare = pBuffer.readByte();

            return new SchematicResultRecipe(pRecipeId, outputCount, inputCount, minDontCare, maxDontCare, output);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull SchematicResultRecipe pRecipe) {
            // pBuffer.writeItem(pRecipe.output);
            // pBuffer.writeInt(pRecipe.truthTable.length);
            // for (int i = 0; i < pRecipe.truthTable.length; i++) {
            //     pBuffer.writeInt(pRecipe.truthTable[i]);
            // }

            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeByte(pRecipe.outputCount());
            pBuffer.writeByte(pRecipe.inputCount());
            pBuffer.writeByte(pRecipe.minDontCare());
            pBuffer.writeByte(pRecipe.maxDontCare());
        }

    }
    
}
