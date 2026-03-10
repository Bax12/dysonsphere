package de.bax.dysonsphere.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.recipes.CargoDeliveryRecipe;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CargoList extends EntryList {

    public CargoList(int x, int y, int width, int height, int itemHeight) {
        super(x, y, width, height, itemHeight);
    }

    public Optional<Entry> getSelectedEntry(){
        if(selectedItem == -1) return Optional.empty();
        return Optional.of((Entry) entryList.get(selectedItem));
    }

    public void setSelectedEntry(CargoDeliveryRecipe recipe){
        if(recipe == null) return;
        for(int i = 0; i < entryList.size(); i++){
            if(((Entry) entryList.get(i)).recipe.equals(recipe)){
                selectedItem = i;
                return;
            }
        }
    }

    public boolean addEntry(CargoDeliveryRecipe recipe){
        return entryList.add(new Entry(recipe));
    }

    public boolean removeEntry(CargoDeliveryRecipe recipe){
        selectedItem = -1;
        return entryList.removeIf((entry) -> {
            return ((Entry) entry).recipe.equals(recipe);
        });
    }

    public void renderHover(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY){
        int hovered = (mouseY - y) / itemHeight;
        if(hovered >= 0 && hovered + scrollOffset < entryList.size()){
            ((Entry) entryList.get(hovered + scrollOffset)).renderHover(guiGraphics, font, mouseX, mouseY);
        }
    }


    public static class Entry implements IEntry {

        public final CargoDeliveryRecipe recipe; 

        public Entry(CargoDeliveryRecipe recipe){
            this.recipe = recipe;
        }

        @Override
        public void render(@Nonnull GuiGraphics pGuiGraphics, int pIndex, int x, int y, int width, int height, int pMouseX, int pMouseY, boolean isSelected, float pPartialTick) {
            MutableComponent comp = !recipe.itemOutput().isEmpty() ? recipe.itemOutput().getDisplayName().copy().append(" " + recipe.itemOutput().getCount() + "x") : recipe.fluidOutput().getDisplayName().copy().append(" " + recipe.fluidOutput().getAmount() + "mB");
            // comp.append(Component.literal("- %sRF".formatted(recipe.energy())));
            AssetUtil.renderMaxWidthString(pGuiGraphics, comp, x, y, width, isSelected ? 0x88444444 : 0xFFFFFFFF, 0x00000000, 0xF000F0, !isSelected);
        }

        public void renderHover(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY){
            List<Component> msg = new ArrayList<>();
            msg.add(Component.literal("Dyson Sphere Energy needed: %sRF".formatted(recipe.energy())));
            msg.add(Component.literal("Required Constructs: "));
            for(var con : recipe.requiredConstructs()){
                msg.add(con.getDisplayName());
            }
            guiGraphics.renderComponentTooltip(font, msg, mouseX, mouseY);
        }

    }
    
}
