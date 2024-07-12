package de.bax.dysonsphere.keybinds;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.network.LaserCooldownSyncRequestPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public class ModKeyBinds {
    
    public static final String DYSONSPHERE_CATEGORY_LASER = "key.dysonsphere.orbital_laser";

    public static final IKeyConflictContext ORBITAL_LASER_INPUT = new IKeyConflictContext() {
        @Override
        public boolean isActive() {
            return ORBITAL_LASER_CONTROL_MAPPING.get().isDown();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return other == KeyConflictContext.IN_GAME;
        }
    };

    public static final Lazy<KeyMapping> ORBITAL_LASER_CONTROL_MAPPING = Lazy.of(() -> new KeyMapping("key.dysonsphere.orbital_laser_control", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, DYSONSPHERE_CATEGORY_LASER));

    public static final Lazy<KeyMapping> ORBITAL_LASER_SEQUENCE_LEFT_MAPPING = Lazy.of(() -> new KeyMapping("key.dysonsphere.orbital_laser_seq_left", ORBITAL_LASER_INPUT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, DYSONSPHERE_CATEGORY_LASER));
    public static final Lazy<KeyMapping> ORBITAL_LASER_SEQUENCE_UP_MAPPING = Lazy.of(() -> new KeyMapping("key.dysonsphere.orbital_laser_seq_up", ORBITAL_LASER_INPUT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UP, DYSONSPHERE_CATEGORY_LASER));
    public static final Lazy<KeyMapping> ORBITAL_LASER_SEQUENCE_RIGHT_MAPPING = Lazy.of(() -> new KeyMapping("key.dysonsphere.orbital_laser_seq_right", ORBITAL_LASER_INPUT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, DYSONSPHERE_CATEGORY_LASER));
    public static final Lazy<KeyMapping> ORBITAL_LASER_SEQUENCE_DOWN_MAPPING = Lazy.of(() -> new KeyMapping("key.dysonsphere.orbital_laser_seq_down", ORBITAL_LASER_INPUT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, DYSONSPHERE_CATEGORY_LASER));




    public static void registerKeyBindings(RegisterKeyMappingsEvent event){
        event.register(ORBITAL_LASER_CONTROL_MAPPING.get());
        event.register(ORBITAL_LASER_SEQUENCE_LEFT_MAPPING.get());
        event.register(ORBITAL_LASER_SEQUENCE_UP_MAPPING.get());
        event.register(ORBITAL_LASER_SEQUENCE_RIGHT_MAPPING.get());
        event.register(ORBITAL_LASER_SEQUENCE_DOWN_MAPPING.get());
    }

    public static void handleKeyPress(ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            if(ORBITAL_LASER_CONTROL_MAPPING.get().isDown()){
                Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                    while (ORBITAL_LASER_SEQUENCE_LEFT_MAPPING.get().consumeClick()) {
                        laser.setCurrentInputSequence(laser.getCurrentInputSequence() + "a");
                    }
                    while (ORBITAL_LASER_SEQUENCE_UP_MAPPING.get().consumeClick()) {
                        laser.setCurrentInputSequence(laser.getCurrentInputSequence() + "w");
                    }
                    while (ORBITAL_LASER_SEQUENCE_RIGHT_MAPPING.get().consumeClick()) {
                        laser.setCurrentInputSequence(laser.getCurrentInputSequence() + "d");
                    }
                    while (ORBITAL_LASER_SEQUENCE_DOWN_MAPPING.get().consumeClick()) {
                        laser.setCurrentInputSequence(laser.getCurrentInputSequence() + "s");
                    }
                });
            } else {
                LocalPlayer player = Minecraft.getInstance().player;
                if(player != null){
                    player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                        laser.setCurrentInputSequence("");
                    });
                    while (ORBITAL_LASER_SEQUENCE_LEFT_MAPPING.get().consumeClick()) {}
                    while (ORBITAL_LASER_SEQUENCE_UP_MAPPING.get().consumeClick()) {}
                    while (ORBITAL_LASER_SEQUENCE_RIGHT_MAPPING.get().consumeClick()) {}
                    while (ORBITAL_LASER_SEQUENCE_DOWN_MAPPING.get().consumeClick()) {}
                }
                
            }
        }
    }

}
