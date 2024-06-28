package de.bax.dysonsphere.blocks;

import com.google.common.base.Supplier;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DysonSphere.MODID);

    public static final DeferredRegister<Item> ITEM_BLOCKS = DeferredRegister.create(ForgeRegistries.ITEMS, DysonSphere.MODID);

    @SuppressWarnings("null")
    public static Properties defaultMetal = Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).destroyTime(0.7f).explosionResistance(6.0f).pushReaction(PushReaction.IGNORE);


    
    public static final RegistryObject<Block> HEAT_PIPE_BLOCK = registerBlock("heat_pipe_block", () -> new HeatPipeBlock());
    public static final RegistryObject<Block> DS_MONITOR_BLOCK = registerBlock("dysonsphere_monitor_block", () -> new DSMonitorBlock());
    public static final RegistryObject<Block> RAILGUN_BLOCK = registerBlock("railgun_block", () -> new RailgunBlock());
    public static final RegistryObject<Block> DS_ENERGY_RECEIVER_BLOCK = registerBlock("dysonsphere_energy_receiver_block", () -> new DSEnergyReceiverBlock());
    public static final RegistryObject<Block> HEAT_EXCHANGER_BLOCK = registerBlock("heat_exchanger_block", () -> new HeatExchangerBlock());//5mb/t if >450° + 1mb/t per 50° over 450
    public static final RegistryObject<Block> HEAT_GENERATOR_BLOCK = registerBlock("heat_generator_block", () -> new HeatGeneratorBlock());//1RF/T per 10°difference
    public static final RegistryObject<Block> LASER_PATTERN_CONTROLLER_BLOCK = registerBlock("laser_pattern_controller_block", () -> new LaserPatternControllerBlock());


    

    public static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> sup) {
        RegistryObject<Block> block = BLOCKS.register(name, sup);
        ITEM_BLOCKS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }


    // public static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> sup, Supplier<? extends Item> itemSup){
    //     ITEM_BLOCKS.register(name, itemSup);
    //     return BLOCKS.register(name, sup);
    // }

}
