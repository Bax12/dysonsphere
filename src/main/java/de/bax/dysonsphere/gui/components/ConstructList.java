package de.bax.dysonsphere.gui.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class ConstructList {
    
    public static final int DISPLAY_ITEM_COUNT = 10;

    protected final List<Entry> entryList = new LinkedList<Entry>();

    protected int x,y,width,height, itemHeight, scrollOffset = 0, highlightedItem = -1;

    public ConstructList(int x, int y, int width, int height, int itemHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemHeight = itemHeight;
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int index = -1;
        boolean hovering = isHovered(pMouseX, pMouseY);
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);
        // for (Entry entry : entryList) {
        for(int i = scrollOffset; i < entryList.size(); i++){
            entryList.get(i).render(pGuiGraphics, ++index, x + 5, this.y + 5 + itemHeight * index, width - 10, itemHeight, pMouseX, pMouseY, hovering, pPartialTick);
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
        return entryList.removeIf((entry) -> {
            return entry.construct.equals(construct);
        });
    }

    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton){
        int clicked = (mouseY - y) / itemHeight;
        if(clicked >= 0 && clicked + scrollOffset < entryList.size()){
            if(entryList.get(clicked + scrollOffset).onMouseClicked(mouseButton)){
                highlightedItem = clicked + scrollOffset;
                for (int i = 0; i < entryList.size(); i++){ //this bothers me...
                    if(i != highlightedItem){
                        entryList.get(i).setHighlighted(false);
                        entryList.get(i).setSelected(false);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void removeHighlighted(){
        entryList.forEach((entry) -> {
            entry.setHighlighted(false);
        });
        highlightedItem = -1;
    }

    public Optional<Entry> getHighlightedEntry(){
        if(highlightedItem == -1) return Optional.empty();
        return Optional.of(entryList.get(highlightedItem));
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

        protected boolean selected; //perform mass operation on multiple selected entries
        protected boolean highlighted; //show details about a single highlighted entry
        protected boolean enabled; //wether the construct in the dysonsphere is currently enabled or disabled


        public Entry(Construct construct){
            this.construct = construct;
        }

        
        @SuppressWarnings("null")
        public void render(@Nonnull GuiGraphics pGuiGraphics, int pIndex, int x, int y, int width, int height, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick) {
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
            AssetUtil.renderMaxWidthString(pGuiGraphics, comp, x, y, width, selected ? 0x88444444 : 0xFFFFFFFF, 0x00000000, 0xF000F0, !selected);
        }

        public boolean isSelected(){
            return selected;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setSelected(boolean selected){
            this.selected = selected;
        }

        public void setHighlighted(boolean highlighted){
            this.highlighted = highlighted;
        }

        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

        @SuppressWarnings("null")
        public boolean onMouseClicked(int button){
            selected = button == 0 ? !selected : false;
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.1f, selected ? 0.8f : 0.4f);
            highlighted = button == 0 || highlighted; //stay highlighted on r-click on same entry.
            return highlighted;
        }

    }

    public static final Entry EMPTY = new Entry(new Construct(){
        public net.minecraft.resources.ResourceLocation getResourceLocation() {
            return new ResourceLocation(DysonSphere.MODID, "empty");
        };
    });




}
