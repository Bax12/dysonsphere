package de.bax.dysonsphere.capabilities.grapplingHook;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookPlayerContainer implements ICapabilityProvider {

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

    public class GrapplingHookContainer implements IGrapplingHookContainer {

        protected Set<GrapplingHookEntity> hooks = new LinkedHashSet<GrapplingHookEntity>();

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




    }
    
}
