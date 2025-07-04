package de.bax.dysonsphere.items;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CapsuleItem extends Item {
    
    public enum TYPE {

        SOLAR_0(10, 0.00001f),
        LASER_0(-50, 0.00001f),
        STRUCTURE_0(0, 0.00001f),
        SOLAR_1(100, 0.000001f),
        LASER_1(-25, 0.000001f),
        STRUCTURE_1(0, 0.000001f),
        SOLAR_2(500, 0.0000001f),
        LASER_2(-5, 0.0000001f),
        STRUCTURE_2(0, 0.0000001f),
        SOLAR_3(1000, 0.00000001f),
        LASER_3(0, 0.00000001f),
        STRUCTURE_3(0, 0.00000001f);

        public int energyProvided;
        public float completionProgress;

        TYPE(int energyProvided, float completionProgress){
            this.energyProvided = energyProvided;
            this.completionProgress = completionProgress;
        }

        public int getTier(){
            return Integer.parseInt(name().substring(name().indexOf("_")+1)); //if this ever breaks, it means something with the names is wrong/changed
        }

        
    }


    protected TYPE type;

    public CapsuleItem(TYPE type) {
        super(new Item.Properties());
        this.type = type;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap.equals(DSCapabilities.DS_PART) ? LazyOptional.of(() -> new IDSPart() {
                    @Override
                    public int getEnergyProvided() {
                        return type.energyProvided;
                    }
                    
                    @Override
                    public float getCompletionProgress() {
                        return type.completionProgress;
                    }
                }).cast() : LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @javax.annotation.Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        if(pLevel != null && pLevel.isClientSide){
            addClientTooltip(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void addClientTooltip(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull  List<Component> pTooltipComponents, @Nonnull  TooltipFlag pIsAdvanced){
        pTooltipComponents.add(Component.literal("Provided Energy: " + this.type.energyProvided));
        pTooltipComponents.add(Component.literal("Provided Completion: " + this.type.completionProgress));
    }

}
