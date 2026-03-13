package de.bax.dysonsphere.gui.components;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConstructList extends EntryList {
    
    protected int highlightedItem = -1;

    public ConstructList(int x, int y, int width, int height, int itemHeight) {
        super(x, y, width, height, itemHeight);

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
            return ((Entry)entry).construct.equals(construct);
        });
    }

    @Override
    public void clear(){
        highlightedItem = -1;
        super.clear();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton){
        if(super.onMouseClicked(mouseX, mouseY, mouseButton)){
            highlightedItem = selectedItem;
            return true;
        }
        return false;
    }

    public void removeHighlighted(){
        highlightedItem = -1;
    }


    public Optional<Entry> getHighlightedEntry(){
        if(highlightedItem == -1) return Optional.empty();
        return Optional.of((Entry) entryList.get(highlightedItem));
    }

    public Optional<Entry> getSelectedEntry(){
        if(selectedItem == -1) return Optional.empty();
        return Optional.of((Entry) entryList.get(selectedItem));
    }

    public Set<Construct> getEnabledConstructs(){
        return entryList.stream().filter((entry) -> {return ((Entry)entry).enabled;}).map((entry) -> {return ((Entry)entry).construct;}).collect(ImmutableSet.toImmutableSet());
    }

    public Set<Construct> getDisabledConstructs(){
        return entryList.stream().filter((entry) -> {return !((Entry)entry).enabled;}).map((entry) -> {return ((Entry)entry).construct;}).collect(ImmutableSet.toImmutableSet());
    }    
    public static class Entry implements IEntry {

        public final Construct construct;

        protected boolean enabled; //wether the construct in the dysonsphere is currently enabled or disabled


        public Entry(Construct construct){
            this.construct = construct;
        }

        
        @SuppressWarnings("null")
        @Override
        public void render(@Nonnull GuiGraphics pGuiGraphics, int pIndex, int x, int y, int width, int height, int pMouseX, int pMouseY, boolean isSelected, float pPartialTick) {
            MutableComponent comp = construct.getDisplayName().copy(); 
            if(enabled){
                comp.withStyle(ChatFormatting.UNDERLINE);
            } 

            AssetUtil.renderMaxWidthString(pGuiGraphics, comp, x, y, width, isSelected ? 0x88444444 : 0xFFFFFFFF, 0x00000000, LightTexture.FULL_BRIGHT, !isSelected);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

    }

    public static final Entry EMPTY = new Entry(new Construct(){
        public net.minecraft.resources.ResourceLocation getResourceLocation() {
            return new ResourceLocation(DysonSphere.MODID, "empty");
        };
    });




}
