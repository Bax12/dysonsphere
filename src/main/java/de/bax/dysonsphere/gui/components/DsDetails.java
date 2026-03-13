package de.bax.dysonsphere.gui.components;

import java.util.Locale;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;

import de.bax.dysonsphere.tileentities.DSMonitorTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DsDetails implements Renderable {

    protected final DSMonitorTile tile;
    protected int x, y, width, height;
    protected boolean dynamicHeight = false;

    public DsDetails(DSMonitorTile tile, int x, int y, int width, int height) {
        this.tile = tile;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    @Override
    public void render(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if(tile == null) return;
        if(tile.getDsCompletionPercentage() == -1 ) return;
        if(dynamicHeight){
            height = 75 + (tile.getDsParts().size() * 10);
        }
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(5);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_status"), x + 2, y + 2, width - 4, 0xFFFFFFFF, 0, LightTexture.FULL_BRIGHT, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_completion", df.format(tile.getDsCompletionPercentage())), x + 2, y + 12, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);

        df.setMaximumFractionDigits(0);

        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_capacity", df.format(tile.getDsEnergy())), x + 2, y + 22, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_parts"), x + 2, y + 32, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);

        int offset = 0;
        for(Entry<Item, Long> entry : tile.getDsParts().entrySet().stream().sorted((a, b) -> {return b.getValue().compareTo(a.getValue());}).toList()){
            AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_part", entry.getKey().getName(ItemStack.EMPTY), entry.getValue()), x + 2, y + 42 + offset, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
            offset += 10;
        }
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_usage", df.format(tile.getDsUsage())), x + 2, y + 52 + offset, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_monitor_power_draw", df.format(tile.getDsEnergyDraw())), x + 2, y + 62 + offset, width - 4, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
    }

    public void setDynamicHeight(boolean dynamicHeight){
        this.dynamicHeight = dynamicHeight;
    }

    public int getHeight(){
        return height;
    }
    
}
