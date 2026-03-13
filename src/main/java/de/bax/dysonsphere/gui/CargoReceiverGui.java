package de.bax.dysonsphere.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.CargoReceiverContainer;
import de.bax.dysonsphere.gui.components.CargoList;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.tileentities.CargoReceiverTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CargoReceiverGui extends BaseGui<CargoReceiverContainer> {

    @Nonnull
    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_cargo_receiver");
    private final CargoReceiverTile tile;
    private FluidDisplay fluid;
    private EditBox inputBox;
    private CargoList cargoList;
    private Button confirmButton;

    public CargoReceiverGui(CargoReceiverContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        
        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 138 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.inputBox = new EditBox(font, this.leftPos + 76, this.topPos + 119, 40, 13, Component.translatable("tooltip.dysonsphere.cargo_receiver_wanted"));

        inputBox.setValue(Integer.toString(tile.getDsPowerDraw()));
        inputBox.setFilter((String text) -> {
            return StringUtils.isNumeric(text) || text.isEmpty();
        });
        inputBox.setEditable(true);
        inputBox.active = true;

        this.cargoList = new CargoList(this.leftPos + 10, this.topPos + 10, 80, 105, 15);
        loadRecipes();

        this.confirmButton = new Button.Builder(Component.translatable("tooltip.dysonsphere.ds_controller_confirm_button"), (button) -> confirmButtonPress()).bounds(this.leftPos + 122, this.topPos + 118, 48, 15).build();

        addRenderableWidget(inputBox);
        addRenderableWidget(confirmButton);

        addRenderableOnly(cargoList);

        this.fluid = new FluidDisplay(this.leftPos + 140, this.topPos + 10, tile.tank);
    }

    protected void confirmButtonPress() {
        int target = 0;
        if(!inputBox.getValue().isEmpty()){
            target = Integer.parseInt(inputBox.getValue());
        }
        tile.setDsPowerDraw(target);

        cargoList.getSelectedEntry().ifPresent((entry) -> {
            tile.setCurrentRecipe(entry.recipe);
        });

        tile.sendGuiUpdate();
    }

    protected void loadRecipes(){
        tile.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.CARGO_DELIVERY_TYPE.get()).forEach((recipe) -> {
            cargoList.addEntry(recipe);
        });
        cargoList.setSelectedEntry(tile.getCurrentRecipe());
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 138, 0, 0, 176, 86);
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 138);


        fluid.draw(pGuiGraphics);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Request Energy"), this.leftPos + 8, this.topPos + 121, 60, 0xFFFFFFFF, 0, LightTexture.FULL_BRIGHT, true);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        fluid.drawOverlay(guiGraphics, mouseX, mouseY);
        
        if(inputBox.isMouseOver(mouseX, mouseY)){
            guiGraphics.renderComponentTooltip(font, List.of(Component.literal("Energy dedicated to fullfilling the request")), mouseX, mouseY);
        }
        guiGraphics.blit(RES_LOC, this.leftPos + 98, this.topPos + 104, 0, 139, tile.getProgressScaled(67), 10);
        if(mouseX >= this.leftPos + 98 && mouseX <= this.leftPos + 164 && mouseY >= this.topPos + 104 && mouseY <= this.topPos + 113){
            List<Component> tooltip = new ArrayList<>();
            switch (tile.getStatus()) {
                case BLOCKED_CONSTRUCTS:
                    tooltip.add(Component.literal("Missing required constructs for delivery"));
                    break;
                case BLOCKED_OUTPUT:
                    tooltip.add(Component.literal("No space in output for delivery"));
                    break;
                case BLOCKED_SKY:
                    tooltip.add(Component.literal("No clear path to deliver from orbit (cannot see the sky)"));
                    break;
                case WORKING:
                case READY:
                    int energyStored = tile.getEnergyStored(), energyRecipe = (tile.getCurrentRecipe() != null) ? tile.getCurrentRecipe().energy() : 0;
                    tooltip.add(Component.literal("Next Delivery: " + AssetUtil.FLOAT_FORMAT.format((tile.isWorking() && energyStored == 0 ? energyRecipe : energyStored)) +  "/" + AssetUtil.FLOAT_FORMAT.format(energyRecipe) + "RF"));
                
            }
            
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }

        if(cargoList.isHovered(mouseX, mouseY)){
            cargoList.renderHover(guiGraphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(cargoList.isHovered((int) pMouseX, (int) pMouseY)){
            cargoList.onMouseClicked((int) pMouseX, (int) pMouseY, pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(cargoList.isHovered((int) pMouseX, (int) pMouseY)){
            cargoList.onMouseScrolled((int) pMouseX, (int) pMouseY, pDelta);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
    
}
