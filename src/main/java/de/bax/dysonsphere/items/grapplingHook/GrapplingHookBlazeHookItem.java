package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookHook;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookBlazeHookItem extends Item {

    public static int countNormal = 2;
    public static float gravityNormal = 0.01f;
    public static int countNether = 4;
    public static float gravityNether = 0f;
    
    public GrapplingHookBlazeHookItem(){
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
                            return level.dimension().equals(Level.NETHER) ? countNether : countNormal;
                        }

                        @Override
                        public boolean canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult) {
                            if(level.getBlockState(hitResult.getBlockPos()).isFlammable(level, hitResult.getBlockPos(), hitResult.getDirection()) || level.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())).getFluidState().is(Fluids.WATER)){
                                // level.getBlockState(hitResult.getBlockPos())
                                BlockPos blockPos1 = hitResult.getBlockPos().relative(hitResult.getDirection());
                                BlockState blockstate1 = BaseFireBlock.getState(level, blockPos1);
                                level.setBlock(blockPos1, blockstate1, 11);
                                level.gameEvent(player, GameEvent.BLOCK_PLACE,  hitResult.getBlockPos());
                                level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.BLAZE_HURT, SoundSource.PLAYERS, 0.5f, 0.8f);
                                hook.recall();
                                return false;
                            }
                            
                            return true;
                        }

                        @Override
                        public float getGravity(Level level, Player player) {
                            return level.dimension().equals(Level.NETHER) ? gravityNether : gravityNormal;
                        }

                        @Override
                        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(null, player, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.3f, 2.0f);
                        }

                        @Override
                        public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 0.5f, 0.8f);
                        }

                        @Override
                        public void onHookRecall(Level level, Player player, GrapplingHookEntity hook) {
                            level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.PLAYERS, 0.5f, 0.8f);
                        }

                        @Override
                        public int getColor() {
                            return 0xffbf00;
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
