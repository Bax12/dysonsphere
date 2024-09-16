package de.bax.dysonsphere.sounds;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DysonSphere.MODID);

    public static final RegistryObject<SoundEvent> RAILGUN_SHOT = SOUNDS.register("railgun_shot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DysonSphere.MODID, "railgun_shot")));
    public static final RegistryObject<SoundEvent> RAILGUN_CHARGE = SOUNDS.register("railgun_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DysonSphere.MODID, "railgun_charge")));
    public static final RegistryObject<SoundEvent> DS_ENERGY_RECEIVER_WORK = SOUNDS.register("ds_energy_receiover_work", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DysonSphere.MODID, "ds_renergy_receiver_work")));
    public static final RegistryObject<SoundEvent> ELECTRIC_WINCH = SOUNDS.register("electric_winch", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(DysonSphere.MODID, "electric_winch")));
}
