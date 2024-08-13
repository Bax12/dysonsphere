package de.bax.dysonsphere.capabilities.grapplingHook;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.network.GrapplingHookSyncPackage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookPlayerContainer implements ICapabilitySerializable<CompoundTag> {

    GrapplingHookContainer container;
    LazyOptional<IGrapplingHookContainer> lazyContainer = LazyOptional.of(() -> container);
    Player containingEntity;

    public GrapplingHookPlayerContainer(Player player){
        this.containingEntity = player;
        container = new GrapplingHookContainer();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.GRAPPLING_HOOK)){
            return lazyContainer.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return container.save();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        container.load(nbt);
    }
    

    public class GrapplingHookContainer implements IGrapplingHookContainer {

        protected Set<GrapplingHookEntity> hooks = new LinkedHashSet<GrapplingHookEntity>();
        protected wireState state = wireState.STOP;

        public static enum wireState {
            STOP,
            PULL,
            UNWIND;

            boolean isUnwinding(){
                return this.equals(UNWIND);
            }

            boolean isPulling(){
                return this.equals(PULL);
            }

            boolean isStopped(){
                return this.equals(STOP);
            }

            
        }

        @Override
        public List<GrapplingHookEntity> getHooks() {
            hooks.removeIf((hook) -> {
                return hook.isRemoved();
            });
            return ImmutableList.copyOf(hooks);
        }

        @Override
        public void addHook(GrapplingHookEntity hook) {
            hooks.add(hook);
        }

        @Override
        public void removeHook(GrapplingHookEntity hook) {
            hooks.remove(hook);
        }

        @Override
        public void deployHook() {
            GrapplingHookEntity hook = new GrapplingHookEntity(containingEntity, containingEntity.level(), 3f);
            this.addHook(hook);
            containingEntity.level().addFreshEntity(hook);
        }

        @Override
        public void recallSingleHook() {
            var hook = getNearestHookToLook(containingEntity.position(), containingEntity.getLookAngle());
            if(hook != null){
                hook.recall();
            }
        }

        @Override
        public void togglePulling() {
            if(state.isPulling()){
                state = wireState.STOP;
            } else {
                state = wireState.PULL;
            }
            if(containingEntity instanceof ServerPlayer sPlayer){
                GrapplingHookSyncPackage.sendSyncPackage(sPlayer, container);
            }
        }

        @Override
        public void toggleUnwinding() {
            if(state.isUnwinding()){
                state = wireState.STOP;
            } else {
                state = wireState.UNWIND;
            }
            if(containingEntity instanceof ServerPlayer sPlayer){
                GrapplingHookSyncPackage.sendSyncPackage(sPlayer, container);
            }
        }

        @Override
        public void stopWinch() {
            if(!state.isStopped()){
                state = wireState.STOP;
                if(containingEntity instanceof ServerPlayer sPlayer){
                    GrapplingHookSyncPackage.sendSyncPackage(sPlayer, container);
                }
            }
        }

        @Override
        public boolean isPulling() {
            return state.isPulling();
        }

        @Override
        public boolean isUnwinding() {
            return state.isUnwinding();
        }

        @Override
        public boolean isStopped() {
            return state.isStopped();
        }

        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            tag.putInt("state", state.ordinal());
            return tag;
        }

        public void load(CompoundTag tag){
            state = wireState.values()[tag.getInt("state")];
        }





    }


}
