package de.bax.dysonsphere.gui;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import de.bax.dysonsphere.gui.components.NumberInput;
import de.bax.dysonsphere.network.LaserPatternSyncPacket;
import de.bax.dysonsphere.network.ModPacketHandler;
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
    protected Button prevIndexButton;
    protected Button nextIndexButton;

    public LaserPatternControllerGui(LaserPatternControllerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.imageWidth = 240;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();


        laserContainer = Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER);
        
        Optional<OrbitalLaserAttackPattern> optionalPattern = laserContainer.map((laser) -> {
            return laser.getActivePatterns().get(0);
        });

        OrbitalLaserAttackPattern pattern = optionalPattern.orElse(new OrbitalLaserAttackPattern());

        patternTextbox = new EditBox(font, getGuiLeft() + 11, getGuiTop() + 22, 70, 16, Component.literal("Pattern:"));
        patternTextbox.setFilter((s) -> {
            return s.matches(OrbitalLaserAttackPattern.validCallInChars);
        });
        
        // countInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 35, 90, 15, Component.literal("Count "), Component.literal(" "), 0, 100, pattern.strikeCount, true);
        // sizeInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 55, 90, 15, Component.literal("Size "), Component.literal(" "), 0, 100, pattern.strikeSize, true);
        // durationInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 75, 90, 15, Component.literal("Duration "), Component.literal(" "), 0, 100, pattern.strikeDuration, true);
        // aimingAreaInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 95, 90, 15, Component.literal("Aim Area "), Component.literal(" "), 0, 100, pattern.aimingArea, true);
        // homingAreaInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 115, 90, 15, Component.literal("Homing Area "), Component.literal(" "), 0, 100, pattern.homingArea, true);
        // homingSpeedInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 135, 90, 15, Component.literal("Homing Speed "), Component.literal(" "), 0, 100, pattern.homingSpeed, true);
        // damageInput = new ForgeSlider(getGuiLeft() + 15, getGuiTop() + 155, 90, 15, Component.literal("Damage "), Component.literal(" "), 0, 100, pattern.damage, true);
        
        // blockDamageInput = new ForgeSlider(getGuiLeft() + 135, getGuiTop() + 35, 90, 15, Component.literal("Block Damage "), Component.literal(" "), 0, 100, pattern.blockDamage, true);
        // startDelayInput = new ForgeSlider(getGuiLeft() + 135, getGuiTop() + 55, 90, 15, Component.literal("Call-in Delay "), Component.literal(" "), 0, 100, pattern.callInDelay, true);
        // repeatDelayInput = new ForgeSlider(getGuiLeft() + 135, getGuiTop() + 75, 90, 15, Component.literal("Wave Delay "), Component.literal(" "), 0, 100, pattern.repeatDelay, true);
        // spreadInput = new ForgeSlider(getGuiLeft() + 135, getGuiTop() + 95, 90, 15, Component.literal("Inaccuracy "), Component.literal(" "), 0, 100, pattern.spreadRadius, true);

        countInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 45, Component.literal("Count"), font, true);
        sizeInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 80, Component.literal("Size"), font);
        durationInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 115, Component.literal("Duration"), font, true);
        aimingAreaInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 150, Component.literal("Aim Area"), font);
        homingAreaInput = new NumberInput(getGuiLeft() + 10, getGuiTop() + 185, Component.literal("Homing Area"), font);

        homingSpeedInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 10, Component.literal("Homing Speed"), font);
        damageInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 45, Component.literal("Damage"), font);
        blockDamageInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 80, Component.literal("Block Damage"), font);
        startDelayInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 115, Component.literal("Call-in Delay"), font, true);
        repeatDelayInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 150, Component.literal("Wave Delay"), font, true);
        spreadInput = new NumberInput(getGuiLeft() + 130, getGuiTop() + 185, Component.literal("Inaccuracy"), font);
        

        displayPattern(pattern);



        applyButton = Button.builder(Component.literal("Apply"), (button) -> {
            applyPattern();
        }).bounds(getGuiLeft() + 83, this.getGuiTop() + 20, 36, 20).build();

        

        prevIndexButton = Button.builder(Component.literal("prev"), (button) -> {
            changeIndex(index - 1);
        }).bounds(this.getGuiLeft() + 12, this.getGuiTop() + 2, 30, 12).build();
        


        nextIndexButton = Button.builder(Component.literal("next"), (button) -> {
            changeIndex(index + 1);
        }).bounds(this.getGuiLeft() + 90, this.getGuiTop() + 2, 30, 12).build();

        laserContainer.ifPresent((laser) -> {
            maxIndex = laser.getActivePatterns().size() - 1;
        });

        if(index == maxIndex){
            nextIndexButton.active = false;
        } else {
            nextIndexButton.active = true;
        }
        if(index == 0){
            prevIndexButton.active = false;
        } else {
            prevIndexButton.active = true;
        }


        
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
        this.addRenderableWidget(prevIndexButton);
        this.addRenderableWidget(nextIndexButton);

        
        
        

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
        var pattern = getCurrentPattern();
        if(pattern.isValid()){
            
            laserContainer.ifPresent((laser) -> {
                //clientside
                laser.setActivePattern(pattern, index);

                //serverside
                ModPacketHandler.INSTANCE.sendToServer(new LaserPatternSyncPacket(laser.getActivePatterns()));
            });
            
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

    protected void changeIndex(int newIndex){
        applyPattern();
        if(newIndex <= maxIndex && newIndex >= 0){
            laserContainer.map((laser) -> {
                return laser.getActivePatterns().get(newIndex);
            }).ifPresent((pattern) -> {
                displayPattern(pattern);
            });
            index = newIndex;
        } 
        // else if(newIndex == maxIndex + 1){
        //     laserContainer.ifPresent((laser) -> {
        //         var pattern = new OrbitalLaserAttackPattern();
        //         laser.addActivePattern(pattern);
        //         displayPattern(pattern);
        //     });
        //     index = newIndex;
        // }
        if(index == maxIndex){
            nextIndexButton.active = false;
        } else {
            nextIndexButton.active = true;
        }
        if(index == 0){
            prevIndexButton.active = false;
        } else {
            prevIndexButton.active = true;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 240, 220);

        guiGraphics.drawString(this.font, Component.literal("Index: " + index), this.getGuiLeft() + 47, this.getGuiTop() + 4, 0xFFFFFF, false);

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
            applyButton.setTooltip(Tooltip.create(Component.literal("Minimum Sequence Length: " + getCurrentPattern().getMinSequenceSize())));
        }

    }

    
    
}
