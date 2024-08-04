// package de.bax.dysonsphere.compat.pneumaticcraft;

// import java.util.Objects;

// import org.jetbrains.annotations.NotNull;
// import org.jetbrains.annotations.Nullable;

// import de.bax.dysonsphere.capabilities.DSCapabilities;
// import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
// import me.desht.pneumaticcraft.common.block.entity.IHeatExchangingTE;
// import net.minecraft.core.Direction;
// import net.minecraftforge.common.capabilities.Capability;
// import net.minecraftforge.common.capabilities.ICapabilityProvider;
// import net.minecraftforge.common.util.LazyOptional;

// //Only use would be to get the HeatGenerator working without DysonSphere Tiles as buffer. Sadly it ignores the transfer limits.
// //Everything else works without it just as well and this is the only reason we'd have to compile against pneumaticcraft and not just its api.
// //TLDR: Wont fix
// public class PNC2DSHeatHandler implements ICapabilityProvider {

//     protected final IHeatExchangingTE tile;
//     // protected final IHeatContainer[] heat = new IHeatContainer[7];
//     protected final LazyOptional<IHeatContainer>[] lazyHeat = new LazyOptional[7];

//     public PNC2DSHeatHandler(IHeatExchangingTE tile){
//         this.tile = tile;
//         for(int i = 0; i < 6; i++){
//             IHeatContainer heat = new PNC2DSHeatAdapter(Direction.values()[i]);
//             // heat[i] = foo;
//             lazyHeat[i] = LazyOptional.of(() -> heat);
//         }
//         IHeatContainer heat = new PNC2DSHeatAdapter(null);
//         lazyHeat[6] = LazyOptional.of(() -> heat);
//     }

//     @Override
//     public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//         if(cap.equals(DSCapabilities.HEAT)){
//             if(side == null){
//                 return lazyHeat[6].cast();
//             }
//             return lazyHeat[side.ordinal()].cast();
//         }
        
//         return LazyOptional.empty();
//     }

//     public class PNC2DSHeatAdapter implements IHeatContainer {

//         protected final  Direction facing;

//         public PNC2DSHeatAdapter(Direction facing){
//             this.facing = facing;
//         }

//         @Override
//         public double receiveHeat(double maxReceive, boolean simulate) {
//             if(!simulate){
//                 Objects.requireNonNullElse(tile.getHeatExchanger(facing), tile.getHeatExchanger(null)).addHeat(maxReceive);
//             }
//             return maxReceive;
//         }

//         @Override
//         public double extractHeat(double maxExtract, boolean simulate) {
//             if(!simulate){
//                 Objects.requireNonNullElse(tile.getHeatExchanger(facing), tile.getHeatExchanger(null)).addHeat(-maxExtract);
//             }
//             return maxExtract;
//         }

//         @Override
//         public double getHeatStored() {
//             return Objects.requireNonNullElse(tile.getHeatExchanger(facing), tile.getHeatExchanger(null)).getTemperature();
//         }

//         @Override
//         public double getMaxHeatStored() {
//             return Objects.requireNonNullElse(tile.getHeatExchanger(facing), tile.getHeatExchanger(null)).getThermalCapacity();
//         }

//         @Override
//         public double getThermalResistance() {
//             return Objects.requireNonNullElse(tile.getHeatExchanger(facing), tile.getHeatExchanger(null)).getThermalResistance();
//         }
    
        
//     }
    
// }
