package de.bax.dysonsphere.datagen.server;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {

    public ItemTagGenerator(PackOutput output, CompletableFuture<Provider> provider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, blockTags, DysonSphere.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        tag(DSTags.itemCoil).add(ModItems.COIL_COPPER.get(), ModItems.COIL_IRON.get());
        tag(DSTags.itemCoilCopper).add(ModItems.COIL_COPPER.get());
        tag(DSTags.itemCoilIron).add(ModItems.COIL_IRON.get());

        tag(DSTags.itemIngot).add(ModItems.INGOT_SMART_ALLOY.get());
        tag(DSTags.itemIngotSmartAlloy).add(ModItems.INGOT_SMART_ALLOY.get());

        tag(DSTags.itemCapsule).add(ModItems.CAPSULE_SOLAR.get(), ModItems.CAPSULE_EMPTY.get(), ModItems.CAPSULE_LASER.get());
        tag(DSTags.itemCapsuleEmpty).add(ModItems.CAPSULE_EMPTY.get());
        tag(DSTags.itemCapsuleSolar).add(ModItems.CAPSULE_SOLAR.get());
        tag(DSTags.itemCapsuleLaser).add(ModItems.CAPSULE_LASER.get());

        tag(DSTags.itemGrapplingHookComponent).add(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get(), ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.get(), ModItems.GRAPPLING_HOOK_ENGINE_MANUAL.get(), ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL.get(), ModItems.GRAPPLING_HOOK_ENGINE_PRESSURE.get(), ModItems.GRAPPLING_HOOK_ENGINE_STEAM.get(), ModItems.GRAPPLING_HOOK_HOOK_BLAZE.get(), ModItems.GRAPPLING_HOOK_HOOK_SLIME.get(), ModItems.GRAPPLING_HOOK_HOOK_SMART_ALLOY.get(), ModItems.GRAPPLING_HOOK_HOOK_WOOD.get(), ModItems.GRAPPLING_HOOK_ROPE_ENDER.get(), Items.CHAIN, Items.STRING, Items.TRIPWIRE_HOOK);
        tag(DSTags.itemGrapplingHookHook).add(ModItems.GRAPPLING_HOOK_HOOK_BLAZE.get(), ModItems.GRAPPLING_HOOK_HOOK_SLIME.get(), ModItems.GRAPPLING_HOOK_HOOK_SMART_ALLOY.get(), ModItems.GRAPPLING_HOOK_HOOK_WOOD.get(), Items.TRIPWIRE_HOOK);
        tag(DSTags.itemGrapplingHookRope).add(ModItems.GRAPPLING_HOOK_ROPE_ENDER.get(), Items.STRING, Items.CHAIN);
        tag(DSTags.itemGrapplingHookEngine).add(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get(), ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.get(), ModItems.GRAPPLING_HOOK_ENGINE_MANUAL.get(), ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL.get(), ModItems.GRAPPLING_HOOK_ENGINE_PRESSURE.get(), ModItems.GRAPPLING_HOOK_ENGINE_STEAM.get());

        tag(DSTags.curioBack).add(ModItems.GRAPPLING_HOOK_HARNESS.get());
        tag(DSTags.curioBelt).add(ModItems.GRAPPLING_HOOK_HARNESS.get());
        tag(DSTags.curioHands).add(ModItems.LASER_CONTROLLER.get());
        tag(DSTags.curioBracelet).add(ModItems.LASER_CONTROLLER.get());
    }
    
    

}
