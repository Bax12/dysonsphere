package de.bax.dysonsphere.gui;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import de.bax.dysonsphere.gui.components.NumberInput;
import de.bax.dysonsphere.network.LaserPatternSyncPacket;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public class LaserPatternControllerGui extends BaseGui<LaserPatternControllerContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_laser_pattern_controller");


    
    protected EditBox patternTextbox;
    protected NumberInput countInput;
    protected NumberInput sizeInput;
    protected NumberInput durationInput;
    protected NumberInput aimingAreaInput;
    protected NumberInput homingAreaInput;
    protected NumberInput homingSpeedInput;
    protected NumberInput damageInput;
    protected NumberInput blockDamageInput;
    protected NumberInput startDelayInput;
    protected NumberInput repeatDelayInput;
    protected NumberInput spreadInput;

    protected int index = 0;
    protected int maxIndex;

    protected LazyOptional<IOrbitalLaserContainer> laserContainer;


    protected Button applyButton;

    protected final LaserPatternControllerTile tile;

    public LaserPatternControllerGui(LaserPatternControllerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;

        this.imageWidth = 240;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();


        // laserContainer = Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER);
        
        //replace with getting the pattern from pattern item in internal inventory
        // Optional<OrbitalLaserAttackPattern> optionalPattern = laserContainer.map((laser) -> {
        //     return laser.getActivePatterns().get(0);
        // });

        

        patternTextbox = new EditBox(font, getGuiLeft() + 11, getGuiTop() + 22, 70, 16, Component.translatable("tooltip.dysonsphere.laser_pattern_call_in"));
        patternTextbox.setFilter((s) -> {
            return s.matches(OrbitalLaserAttackPattern.validCallInChars);
        });
        
        countInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 45, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_count"), font, true);
        sizeInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 80, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_size"), font);
        durationInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 115, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_duration"), font, true);
        aimingAreaInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 150, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_aim_area"), font);
        homingAreaInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 185, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_homing_area"), font);

        homingSpeedInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 10, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_homing_speed"), font);
        damageInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 45, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_damage"), font);
        blockDamageInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 80, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_block_damage"), font);
        startDelayInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 115, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_start_delay"), font, true);
        repeatDelayInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 150, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_repeat_delay"), font, true);
        spreadInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 185, Component.translatable("tooltip.dysonsphere.laser_pattern_controller_spread"), font);
        



        ItemStack stack = tile.inventory.getStackInSlot(0);

        if(!stack.isEmpty()){
            var foo = stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER);
            Optional<OrbitalLaserAttackPattern> optionalPattern = Optional.empty();
            try {
                optionalPattern = foo.map((container) -> {
                    return container.getPattern();
                });
            } catch(Exception e) {
                DysonSphere.LOGGER.debug("LaserPatterControllerGui init Exception: {}", e);
            }
            
    
            OrbitalLaserAttackPattern pattern = optionalPattern.orElse(new OrbitalLaserAttackPattern());
    
            displayPattern(pattern);
        }
        



        applyButton = Button.builder(Component.translatable("tooltip.dysonsphere.laser_pattern_controller_apply"), (button) -> {
            applyPattern();
        }).bounds(getGuiLeft() + 83, this.getGuiTop() + 20, 36, 20).build();

        

        
        this.addRenderableWidget(patternTextbox);
        this.addRenderableWidget(countInput);
        this.addRenderableWidget(sizeInput);
        this.addRenderableWidget(durationInput);
        this.addRenderableWidget(aimingAreaInput);
        this.addRenderableWidget(homingAreaInput);
        this.addRenderableWidget(homingSpeedInput);
        this.addRenderableWidget(damageInput);
        this.addRenderableWidget(blockDamageInput);
        this.addRenderableWidget(startDelayInput);
        this.addRenderableWidget(repeatDelayInput);
        this.addRenderableWidget(spreadInput);
        this.addRenderableWidget(applyButton);
        
    }

    protected OrbitalLaserAttackPattern getCurrentPattern(){
        OrbitalLaserAttackPattern pattern  = new OrbitalLaserAttackPattern();
        pattern.setCallInSequence(patternTextbox.getValue());
        pattern.strikeCount = countInput.getValueInt();
        pattern.strikeSize = sizeInput.getValue();
        pattern.strikeDuration = durationInput.getValueInt();
        pattern.aimingArea = aimingAreaInput.getValue();
        pattern.homingArea = homingAreaInput.getValue();
        pattern.homingSpeed = homingSpeedInput.getValue();
        pattern.damage = damageInput.getValue();
        pattern.blockDamage = blockDamageInput.getValue();
        pattern.callInDelay = startDelayInput.getValueInt();
        pattern.repeatDelay = repeatDelayInput.getValueInt();
        pattern.spreadRadius = spreadInput.getValue();
        
        pattern.finishPattern();
        return pattern;
    }

    protected void applyPattern(){
        if(tile.inventory.getStackInSlot(0).isEmpty()) return;
        var pattern = getCurrentPattern();
        if(pattern.isValid()){
            
            // laserContainer.ifPresent((laser) -> {
            //     //clientside
            //     laser.setActivePattern(pattern, index);

            //     //serverside
            //     ModPacketHandler.INSTANCE.sendToServer(new LaserPatternSyncPacket(laser.getActivePatterns()));
            // });

            // tile.inventory.getStackInSlot(0).copy().getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((container) -> {
            //     container.setPattern(pattern);
            // });
            ModPacketHandler.INSTANCE.sendToServer(new LaserPatternSyncPacket(pattern, tile.getBlockPos()));
            
        } else {
            var player = Minecraft.getInstance().player;
            player.displayClientMessage(Component.literal("Pattern invalid, try extending the call-in sequence"), true);
            // player.playSound(SoundEvents.DISPENSER_FAIL);
            Minecraft.getInstance().level.playLocalSound(player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 10, 1, true);
        }
        
    }

    protected void displayPattern(OrbitalLaserAttackPattern pattern){
        countInput.setValue(pattern.strikeCount);
        sizeInput.setValue(pattern.strikeSize);
        durationInput.setValue(pattern.strikeDuration);
        aimingAreaInput.setValue(pattern.aimingArea);
        homingAreaInput.setValue(pattern.homingArea);
        homingSpeedInput.setValue(pattern.homingSpeed);
        damageInput.setValue(pattern.damage);
        blockDamageInput.setValue(pattern.blockDamage);
        startDelayInput.setValue(pattern.callInDelay);
        repeatDelayInput.setValue(pattern.repeatDelay);
        spreadInput.setValue(pattern.spreadRadius);

        patternTextbox.setValue(pattern.getCallInSequence());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 240, 220);

    }

    // @Override
    // public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
    //     children().forEach((widget) -> {
    //         if(widget.isMouseOver(mouseX, mouseY)){
    //             widget.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    //         }
    //     });
    //     return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    // }


    @Override
    public void onClose() {
        super.onClose();
        applyPattern();
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if(applyButton.isMouseOver(pMouseX, pMouseY)){
            applyButton.setTooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.laser_pattern_controller_min_sequence", getCurrentPattern().getMinSequenceSize())));
        }

    }

    
    
}
