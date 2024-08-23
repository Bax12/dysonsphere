package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookSlimeHookItem extends Item {
    
    public GrapplingHookSlimeHookItem(){
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
                            return 2;
                        }

                        @Override
                        public boolean canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult) {
                            BlockState blockState = level.getBlockState(hitResult.getBlockPos());
                            
                            if(!blockState.canStickTo(Blocks.SLIME_BLOCK.defaultBlockState()) || !level.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())).getFluidState().isEmpty()){
                                hook.recall();
                                level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.PLAYERS, 0.5f, 0.8f);
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
                            level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 0.5f, 0.8f);
                        }

                        @Override
                        public void onHookRecall(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.PLAYERS, 0.5f, 0.8f);
                        }

                        @Override
                        public int getColor() {
                            return 0x8ad580;
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
