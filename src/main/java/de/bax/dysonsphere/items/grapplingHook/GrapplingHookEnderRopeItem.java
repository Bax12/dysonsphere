package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookRope;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookEnderRopeItem extends Item{
    
    public static float MAX_DISTANCE = 256f;
    public static float LAUNCH_FORCE = 0.1f;
    public static float WINCH_FORCE = 1f;
    public static float GRAVITY = 0.2f;

    public GrapplingHookEnderRopeItem(){
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        // TODO Auto-generated method stub
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ROPE)){
                    return LazyOptional.of(() -> new GrapplingHookEnderRopeWrapper()).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    public static class GrapplingHookEnderRopeWrapper implements IGrapplingHookRope {

        @Override
        public float getMaxDistance(Level level, Player player) {
            return MAX_DISTANCE;
        }

        @Override
        public float getLaunchForceMultiplier(Level level, Player player) {
            return LAUNCH_FORCE;
        }

        @Override
        public float getWinchForceMultiplier(Level level, Player player) {
            return WINCH_FORCE;
        }

        @Override
        public float getHookGravityMultiplier(Level level, Player player) {
            return GRAVITY;
        }

        @Override
        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
            // BlockHitResult result = level.isBlockInLine(new ClipBlockStateContext(player.getLookAngle().scale(5).add(player.getEyePosition(0)), player.getEyePosition(0), (block) -> {
            //     return !block.getFluidState().is(Fluids.WATER);
            // }));

            BlockHitResult result = level.clip(new ClipContext(player.getEyePosition(0), player.getLookAngle().scale(getMaxDistance(level, player)).add(player.getEyePosition(0)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if(result.getType().equals(BlockHitResult.Type.BLOCK)){
                hook.setPos(result.getLocation());
                level.playSound(null, player, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8f, 1.0f);
            } else {
                hook.discard();
            }
        }

        @Override
        public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
            
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            
        }

        @Override
        public void onRappelTick(Level level, Player player) {
            
        }

        @Override
        public int getColor() {
            return 0x0a4d42;
        }

    }

}
