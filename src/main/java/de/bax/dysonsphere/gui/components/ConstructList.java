package de.bax.dysonsphere.gui.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class ConstructList implements Renderable {
    
    public static final int DISPLAY_ITEM_COUNT = 10;

    protected final List<Entry> entryList = new LinkedList<Entry>();

    protected int x,y,width,height, itemHeight, scrollOffset = 0, highlightedItem = -1, selectedItem = -1;

    public ConstructList(int x, int y, int width, int height, int itemHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemHeight = itemHeight;
    }

    @Override
    public void render(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int index = -1;
        // boolean hovering = isHovered(pMouseX, pMouseY);
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);
        // for (Entry entry : entryList) {
        for(int i = scrollOffset; i < entryList.size(); i++){
            entryList.get(i).render(pGuiGraphics, ++index, x + 5, this.y + 5 + itemHeight * index, width - 10, itemHeight, pMouseX, pMouseY, (i == selectedItem), pPartialTick);
        }
        
    }

    public boolean addEntry(Construct construct){
        return entryList.add(new Entry(construct));
    }

    public boolean addEntry(Construct construct, boolean enabled){
        Entry entry = new Entry(construct);
        entry.setEnabled(enabled);
        return entryList.add(entry);
    }

    public boolean removeEntry(Construct construct){
        highlightedItem = -1; //index may have changed. Reset is easiest fix.
        selectedItem = -1;
        return entryList.removeIf((entry) -> {
            return entry.construct.equals(construct);
        });
    }

    public void clear(){
        highlightedItem = -1;
        selectedItem = -1;
        entryList.clear();
    }

    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton){
        int clicked = (mouseY - y) / itemHeight;
        if(clicked >= 0 && clicked + scrollOffset < entryList.size()){
            if(entryList.get(clicked + scrollOffset).onMouseClicked(mouseButton, (selectedItem == clicked + scrollOffset))){
                highlightedItem = selectedItem = clicked + scrollOffset;
                // for (int i = 0; i < entryList.size(); i++){ //this bothers me...
                //     if(i != highlightedItem){
                //         // entryList.get(i).setHighlighted(false);
                //         entryList.get(i).setSelected(false);
                //     }
                // }
                return true;
            }
        }
        return false;
    }

    public void removeHighlighted(){
        // entryList.forEach((entry) -> {
        //     entry.setHighlighted(false);
        // });
        highlightedItem = -1;
    }

    public Optional<Entry> getHighlightedEntry(){
        if(highlightedItem == -1) return Optional.empty();
        return Optional.of(entryList.get(highlightedItem));
    }

    public Optional<Entry> getSelectedEntry(){
        if(selectedItem == -1) return Optional.empty();
        return Optional.of(entryList.get(selectedItem));
    }

    public Set<Construct> getEnabledConstructs(){
        return entryList.stream().filter((entry) -> {return entry.enabled;}).map((entry) -> {return entry.construct;}).collect(ImmutableSet.toImmutableSet());
    }

    public Set<Construct> getDisabledConstructs(){
        return entryList.stream().filter((entry) -> {return !entry.enabled;}).map((entry) -> {return entry.construct;}).collect(ImmutableSet.toImmutableSet());
    }

    public void onMouseScrolled(int mouseX, int mouseY, double scrollDelta){
        // scrollOffset += scrollDelta > 0 ? 1 : -1;
        // if(scrollOffset < 0){
        //     scrollOffset = 0;
        // } else if (scrollOffset + DISPLAY_ITEM_COUNT > entryList.size()) {
        //     scrollOffset = entryList.size() - DISPLAY_ITEM_COUNT;
        // }
    }

    
    public static class Entry {

        public final Construct construct;

        // protected boolean selected; //perform operation on selected entry
        // protected boolean highlighted; //show details about a single highlighted entry
        protected boolean enabled; //wether the construct in the dysonsphere is currently enabled or disabled


        public Entry(Construct construct){
            this.construct = construct;
        }

        
        @SuppressWarnings("null")
        public void render(@Nonnull GuiGraphics pGuiGraphics, int pIndex, int x, int y, int width, int height, int pMouseX, int pMouseY, boolean isSelected, float pPartialTick) {
            MutableComponent comp = construct.getDisplayName().copy(); 
            if(enabled){
                // comp.append(Component.literal(": Active"));
                comp.withStyle(ChatFormatting.UNDERLINE);
            } else {
                // comp.append(Component.literal(": Inactive"));
            }
            // if(highlighted){
            //     comp.withStyle(ChatFormatting.UNDERLINE);
            // }

            // AssetUtil.renderMaxWidthString(pGuiGraphics, comp, x, y, width, selected ? 0x88444444 : 0xFFFFFFFF, 0x22FF0000, 0x00000000, false);
            AssetUtil.renderMaxWidthString(pGuiGraphics, comp, x, y, width, isSelected ? 0x88444444 : 0xFFFFFFFF, 0x00000000, 0xF000F0, !isSelected);
        }

        // public boolean isSelected(){
        //     return selected;
        // }

        // public boolean isHighlighted() {
        //     return highlighted;
        // }

        public boolean isEnabled() {
            return enabled;
        }

        // public void setSelected(boolean selected){
        //     this.selected = selected;
        // }

        // public void setHighlighted(boolean highlighted){
        //     this.highlighted = highlighted;
        // }

        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

        @SuppressWarnings("null")
        public boolean onMouseClicked(int button, boolean isSelected){
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.1f, isSelected ? 0.8f : 0.4f);
            return button == 0;
        }

    }

    public static final Entry EMPTY = new Entry(new Construct(){
        public net.minecraft.resources.ResourceLocation getResourceLocation() {
            return new ResourceLocation(DysonSphere.MODID, "empty");
        };
    });




}
