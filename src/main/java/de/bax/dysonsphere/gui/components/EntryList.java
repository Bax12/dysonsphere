package de.bax.dysonsphere.gui.components;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.sounds.SoundEvents;

public class EntryList implements Renderable {

    protected final List<IEntry> entryList = new LinkedList<IEntry>();

    protected int x,y,width,height, itemHeight, scrollOffset = 0, selectedItem = -1;

    public EntryList(int x, int y, int width, int height, int itemHeight){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemHeight = itemHeight;
    }

    public boolean addEntry(IEntry entry){
        return entryList.add(entry);
    }

    public boolean removeEntry(IEntry entry){
        return entryList.remove(entry);
    }

    public void clear(){
        selectedItem = -1;
        entryList.clear();
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton){
        int clicked = (mouseY - y) / itemHeight;
        if(clicked >= 0 && clicked + scrollOffset < entryList.size()){
            if(entryList.get(clicked + scrollOffset).onMouseClicked(mouseButton, (selectedItem == clicked + scrollOffset))){
                selectedItem = clicked + scrollOffset;
                return true;
            }
        }
        return false;
    }




    public void onMouseScrolled(int mouseX, int mouseY, double scrollDelta){
        scrollOffset += scrollDelta < 0 ? 1 : -1;
        if(scrollOffset < 0){
            scrollOffset = 0;
        } else if (scrollOffset + (height / itemHeight)  > entryList.size()) {
            scrollOffset = Math.max(0, entryList.size() - (height / itemHeight));
        } else {
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.1f, 1.2f);
        }
        
    }


    public boolean isHovered(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    @Override
    public void render(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int index = -1;
        
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);
        
        int i;
        for(i = scrollOffset; (i-scrollOffset+1) * itemHeight < height && i < entryList.size(); i++){
            entryList.get(i).render(pGuiGraphics, ++index, x + 5, this.y + 5 + itemHeight * index, width - 10, itemHeight, pMouseX, pMouseY, (i == selectedItem), pPartialTick);
        }
        if(i < entryList.size()){
            //render scroll indicator down
            pGuiGraphics.fillGradient(x+1, y+height-2, x+width-1, y+height+3, 0xFFAAAAAA, 0xFFc6c6c6);
            // AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("..."), x, y, width, 0xFFFFFFFF, 0x00000000, LightTexture.FULL_BRIGHT, true);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, "...", x + (width/2), y + height - 6, 0xFFFFFFFF);
        }
        if(scrollOffset > 0){
            //render scroll indicator up
            pGuiGraphics.fillGradient(x+1, y-2, x+width-1, y+3, 0xFFc6c6c6, 0xFFAAAAAA);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, "...", x + (width/2), y - 6, 0xFFFFFFFF);
        }
    }

    public static interface IEntry {
        public void render(@Nonnull GuiGraphics pGuiGraphics, int pIndex, int x, int y, int width, int height, int pMouseX, int pMouseY, boolean isSelected, float pPartialTick);

        @SuppressWarnings("null")
        public default boolean onMouseClicked(int button, boolean isSelected){
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.1f, isSelected ? 0.8f : 0.4f);
            return button == 0;
        }
    }
    
}
