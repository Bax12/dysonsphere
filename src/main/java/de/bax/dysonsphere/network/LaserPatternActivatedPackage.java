package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.items.LaserControllerItem;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.items.TargetDesignatorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserPatternActivatedPackage {
    
    protected final OrbitalLaserAttackPattern pattern;

    public LaserPatternActivatedPackage(OrbitalLaserAttackPattern pattern){
        this.pattern = pattern;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(pattern.serializeNBT());
    }

    public static LaserPatternActivatedPackage decode(FriendlyByteBuf buf){
        OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
        pattern.deserializeNBT(buf.readNbt());
        return new LaserPatternActivatedPackage(pattern);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                for(ItemStack stack : player.getInventory().items) {
                    if(stack.is(ModItems.LASER_CONTROLLER.get())){
                        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent((energy) -> {
                            
                            if(energy.extractEnergy(LaserControllerItem.usage, true) == LaserControllerItem.usage){
                                energy.extractEnergy(LaserControllerItem.usage, false);
                                createTargetDesignator(player, pattern);
                            }
                        });
                        break;
                    }
                }
                
            });
        }
    }

    protected static void createTargetDesignator(Player player, OrbitalLaserAttackPattern pattern){
        ItemStack targetDesignator = new ItemStack(ModItems.TARGET_DESIGNATOR.get());
        ItemStack containedStack = player.getMainHandItem();
        if(containedStack.is(ModItems.TARGET_DESIGNATOR.get())){
            containedStack = TargetDesignatorItem.getContainedStack(containedStack);
        }
        TargetDesignatorItem.setContainedStack(targetDesignator, containedStack);
        TargetDesignatorItem.setOrbitalStrikePattern(targetDesignator, pattern);
        player.setItemInHand(InteractionHand.MAIN_HAND, targetDesignator);
    }

}
