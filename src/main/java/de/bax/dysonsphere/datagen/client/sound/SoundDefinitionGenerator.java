package de.bax.dysonsphere.datagen.client.sound;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.sounds.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundDefinitionGenerator extends SoundDefinitionsProvider {

    public SoundDefinitionGenerator(PackOutput output, ExistingFileHelper helper) {
        super(output, DysonSphere.MODID, helper);
    }

    @SuppressWarnings("null")
    @Override
    public void registerSounds() {
        add(ModSounds.RAILGUN_SHOT, definition()
        .subtitle("sound.dysonsphere.railgun_shot")
        .with(sound(ModSounds.RAILGUN_SHOT.get().getLocation())));    
        add(ModSounds.RAILGUN_CHARGE, definition()
        .subtitle("sound.dysonsphere.railgun_charge")
        .with(sound(ModSounds.RAILGUN_CHARGE.get().getLocation()).volume(0.5)));
    }
    
}
