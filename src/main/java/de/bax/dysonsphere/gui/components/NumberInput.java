package de.bax.dysonsphere.gui.components;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.random.RandomGenerator.StreamableGenerator;

import org.joml.Math;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NumberInput extends AbstractWidget {

    public static final int width = 105;
    public static final int height = 30;

    

    protected Button plusButton;
    protected Button minusButton;

    protected EditBox textInput;

    protected final Font font;

    protected List<AbstractWidget> parts; 

    public boolean intOnly = false;

    protected final int minValue;

    NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);

    public NumberInput(int pX, int pY, Component pMessage, Font font) {
        this(pX, pY, pMessage, font, false, 0);
    }

    public NumberInput(int pX, int pY, Component pMessage, Font font, boolean intOnly) {
        this(pX, pY, pMessage, font, intOnly, 0);
    }

    public NumberInput(int pX, int pY, Component pMessage, Font font, int minValue) {
        this(pX, pY, pMessage, font, false, minValue);
    }

    public NumberInput(int pX, int pY, Component pMessage, Font font, boolean intOnly, int minValue) {
        super(pX, pY, width, height, pMessage);


        this.font = font;
        this.intOnly = intOnly;
        this.minValue = minValue;
        
        minusButton = Button.builder(Component.translatable("tooltip.dysonsphere.number_input.minus_button"), (button) -> {
            changeValueBy(-1);
        }).pos(getX() + 3, this.getY() + 13).size(29, 15).build();

        textInput = new EditBox(font, getX() + 35, this.getY() + 12, 35, 15, pMessage);
        
        textInput.setFilter(intOnly ? 
        (s) -> {
            return s.matches("[-+]?[0-9]+") && Integer.parseInt(s) >= minValue;
        }
        :
        (s) -> {
            return s.matches("[-+]?[0-9]*\\.?([0-9]{1,3})") && Float.parseFloat(s) >= minValue;
        });

        

        plusButton = Button.builder(Component.translatable("tooltip.dysonsphere.number_input.plus_button"), (button) -> {
            changeValueBy(1);
        }).pos(getX() + 73, this.getY() + 13).size(29, 15).build();
        

        parts = List.of(plusButton, minusButton, textInput);

        
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        
        pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + width, this.getY() + height, 0xFFFFFFFF);
        
        pGuiGraphics.drawString(font, getMessage(), this.getX() + (int)(width / 2f - font.width(getMessage()) / 2f), this.getY() + 2, 0xFF000000, false);
        plusButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        minusButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        textInput.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        float buttonValue = 1;
        if(Screen.hasShiftDown()){
            buttonValue *= 5;
        }
        if(Screen.hasControlDown()){
            buttonValue *= 10;
        }
        if(!intOnly && Screen.hasAltDown()){
            buttonValue /= 100;
        }
        plusButton.setMessage(Component.translatable("tooltip.dysonsphere.number_input.plus_button", format.format(buttonValue)));
        minusButton.setMessage(Component.translatable("tooltip.dysonsphere.number_input.minus_button", format.format(buttonValue)));
        
        
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        textInput.updateNarration(pNarrationElementOutput);
    }

    public float getValue(){
        float value = 0;
        try{
            value = intOnly ? Integer.parseInt(textInput.getValue()) : Float.parseFloat(textInput.getValue());
        } catch(NumberFormatException e){

        }
        return value;
    }

    public int getValueInt(){
        return (int) getValue();
    }

    public void setValue(float value) {
        // DysonSphere.LOGGER.info("NumberInput setValue pre value: {}", value);
        textInput.setValue(intOnly ? Integer.toString((int) value) : Float.toString(value));
        // DysonSphere.LOGGER.info("NumberInput setValue post value: {}", getValue());
    }

    protected void changeValueBy(float increaseValue){
        if(Screen.hasShiftDown()){
            increaseValue *= 5;
        }
        if(Screen.hasControlDown()){
            increaseValue *= 10;
        }
        if(!intOnly && Screen.hasAltDown()){
            increaseValue /= 100;
        }
        setValue(getValue() + increaseValue);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        parts.forEach((widget) -> {
            if(widget.isMouseOver(pMouseX, pMouseY)) {
                widget.onClick(pMouseX, pMouseY);
            }
        });
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return textInput.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return textInput.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public void setFocused(boolean pFocused) {
        super.setFocused(pFocused);
        textInput.setFocused(pFocused);
    }



    
}
