package de.bax.dysonsphere.items.tools;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.curio.Curios;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class PocketDynamoItem extends Item {

    public static int capacity = 5000;
    public static int productionRate = 5;
    public static int minDelay = 20;
    public static int minDistance = 1;

    public PocketDynamoItem() {
        super(new Item.Properties());
        
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(ForgeCapabilities.ENERGY)){
                    return LazyOptional.of(side == null ? () -> new ItemEnergyHandler(stack, capacity) : () -> new ItemEnergyHandler(stack, capacity){
                        @Override
                        public boolean canReceive() {
                            return false;
                        };
                    }).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int pSlotId, boolean pIsSelected) {
        if(!level.isClientSide()){
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent((energy) -> {
                CompoundTag tag = stack.getOrCreateTag();
                long lastGenTick = tag.getLong("lastGen");
                BlockPos lastGenPos = BlockPos.of(tag.getLong("lastPos"));
                // DysonSphere.LOGGER.debug("PocketDynamo invTick tick: {}, lastTick: {}, pos: {}, lastPos {}", level.getGameTime(), lastGenTick, entity.getOnPos(), lastGenPos);
                if(level.getGameTime() - lastGenTick  > minDelay && entity.getOnPos().distSqr(lastGenPos) > minDistance){
                    energy.receiveEnergy(productionRate, false);
                    tag.putLong("lastGen", level.getGameTime());
                    tag.putLong("lastPos", entity.getOnPos().asLong());
                    stack.setTag(tag);
                }

                chargeInventory(energy, entity);
            });
        }
    }

    protected void chargeInventory(IEnergyStorage energy, Entity entity){
        if(energy.getEnergyStored() > 0 && entity instanceof Player player) {
            for (ItemStack targetStack : player.getInventory().offhand) {
                transferEnergy(energy, targetStack);
            };
            for (ItemStack targetStack : player.getInventory().armor) {
                transferEnergy(energy, targetStack);
            };
            for (ItemStack targetStack : player.getInventory().items) {
                transferEnergy(energy, targetStack);
            };
            if(ModCompat.isLoaded(ModCompat.MODID.CURIOS)){
                Curios.getMatchingCurios(player, (itemStack) -> {return itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent();}).forEach((targetStack) -> {
                    transferEnergy(energy, targetStack);
                });
            }
        }
    }

    protected void transferEnergy(IEnergyStorage energy, ItemStack targetStack){
        targetStack.getCapability(ForgeCapabilities.ENERGY).ifPresent((targetEnergy) -> {
            int transfer = targetEnergy.receiveEnergy(energy.extractEnergy(Integer.MAX_VALUE, true), true);
            if(transfer > 0){
                targetEnergy.receiveEnergy(energy.extractEnergy(transfer, false), false);
            }
        });
    }


    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }



    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand pUsedHand) {
        ItemStack stack = player.getItemInHand(pUsedHand);
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent((energy) -> {
            CompoundTag tag = stack.getOrCreateTag();
            long lastGenTick = tag.getLong("lastGen");
            if(level.getGameTime() - lastGenTick > minDelay){
                energy.receiveEnergy(productionRate, false);
                tag.putLong("lastGen", level.getGameTime());
                tag.putLong("lastPos", player.getOnPos().asLong());
                stack.setTag(tag);
            }
        });
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(energy.getEnergyStored()), AssetUtil.FLOAT_FORMAT.format(energy.getMaxEnergyStored())));
        });
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(@Nonnull ItemStack pStack) {
        return 0xDD2222;
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ENERGY).map((energy) -> {
            return 13 * energy.getEnergyStored() / energy.getMaxEnergyStored();
        }).orElse(0);
    }




    
}
