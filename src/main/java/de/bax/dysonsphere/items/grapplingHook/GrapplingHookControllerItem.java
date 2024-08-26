package de.bax.dysonsphere.items.grapplingHook;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;

public class GrapplingHookControllerItem extends Item {

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    
    public GrapplingHookControllerItem(){
        super(new Item.Properties());

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier("block_reach", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier("entity_reach", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        defaultModifiers = builder.build();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, @Nonnull Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

        if(pPlayer.isShiftKeyDown()){
            pPlayer.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.recallAll();
            });
        } else {
            // spawnHookEntity(itemStack, level, player, 2.5f);
            pPlayer.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.recallSingleHook();
            });
        }
        return InteractionResultHolder.consume(itemStack);
    }

    //triggers a lot more then we need it to. The reduced interaction range is to completely prevent block and entity interactions.
    //therefore removing a lot of variables and making the behavior easier to understand
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        //TODO: find a way to prevent triggering on dropping item from gui, this doesnt work
        // if(entity instanceof Player player){
        //     if(player.inventoryMenu.active){
        //         return true;
        //     }
        // }
        entity.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
            hookContainer.deployHook();
        });
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer) {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }



    

    

    

}
