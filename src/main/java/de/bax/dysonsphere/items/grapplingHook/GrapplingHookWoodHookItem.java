package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookHook;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookWoodHookItem extends Item {
    
    public GrapplingHookWoodHookItem(){
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_HOOK)){
                    return LazyOptional.of(() -> new IGrapplingHookHook() {

                        @Override
                        public int getMaxHookCount(Level level, Player player) {
                            return level.dimension().equals(Level.NETHER) ? 0 : 1;
                        }

                        @Override
                        public boolean canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult) {
                            BlockState state = level.getBlockState(hitResult.getBlockPos());
                            if(!TierSortingRegistry.isCorrectTierForDrops(Tiers.WOOD, state) || state.isBurning(level, hitResult.getBlockPos()) || level.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())).getFluidState().is(Fluids.LAVA)){
                                hook.recall();
                                level.playSound(player, hook.getPosition(0).x, hook.getPosition(0).y, hook.getPosition(0).z, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 0.7f, 0.8f);
                                return false;
                            }
                            return true;
                        }

                        @Override
                        public float getGravity(Level level, Player player) {
                            return 0.05f;
                        }

                        @Override
                        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
                        }

                        @Override
                        public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(player, hook.getPosition(0).x, hook.getPosition(0).y, hook.getPosition(0).z, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 0.5f, 1.2f);
                        }

                        @Override
                        public void onHookRecall(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(player, hook.getPosition(0).x, hook.getPosition(0).y, hook.getPosition(0).z, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.PLAYERS, 0.5f, 1.2f);
                        }

                        @Override
                        public int getColor() {
                            return 0x7d6137;
                        }

                        @Override
                        public ItemStack getHookIcon(Level level, Player player) {
                            return stack;
                        }
                        
                    }).cast();
                }
                
                return LazyOptional.empty();
            }
        };
    }

}
