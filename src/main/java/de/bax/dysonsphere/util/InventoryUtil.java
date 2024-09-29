package de.bax.dysonsphere.util;

import java.util.List;

import com.google.common.base.Predicate;

import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.curio.Curios;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil {
    

    public static boolean isInExtendedPlayerInventory(Player player, Predicate<ItemStack> filter){
        if(player.getInventory().hasAnyMatching(filter)) return true;
        if(ModCompat.isLoaded(ModCompat.MODID.CURIOS)){
            if(!Curios.getMatchingCurios(player, filter).isEmpty()) return true;
        }
        return false;
    }

    public static List<ItemStack> getAllInExtendedPlayerInventory(Player player, Predicate<ItemStack> filter){
        List<ItemStack> itemList = NonNullList.create();
        itemList.addAll(player.getInventory().items);
        itemList.addAll(player.getInventory().offhand);//offhand is not a part of all items...
        itemList.addAll(player.getInventory().armor);
        if(ModCompat.isLoaded(ModCompat.MODID.CURIOS)){
            itemList.addAll(Curios.getMatchingCurios(player, filter));
        }
        return itemList.stream().filter(filter).toList();
    }

}
