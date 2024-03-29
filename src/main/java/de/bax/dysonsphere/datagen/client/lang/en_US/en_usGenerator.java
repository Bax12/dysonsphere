package de.bax.dysonsphere.datagen.client.lang.en_US;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class en_usGenerator extends LanguageProvider{

    public en_usGenerator(PackOutput output) {
        super(output, DysonSphere.MODID, "en_us");
        
    }

    @SuppressWarnings("null")
    @Override
    protected void addTranslations() {
        //blocks
        addBlock(ModBlocks.HEAT_PIPE_BLOCK, "Thermal Coupler");
        addBlock(ModBlocks.DS_MONITOR_BLOCK, "Dyson Sphere Observer");
        addBlock(ModBlocks.RAILGUN_BLOCK, "Electromagnetic Orbital Launcher");
        addBlock(ModBlocks.DS_ENERGY_RECEIVER_BLOCK, "Dyson Sphere Energy Receiver");
        addBlock(ModBlocks.HEAT_EXCHANGER_BLOCK, "Thermal Exchanger");
        addBlock(ModBlocks.HEAT_GENERATOR_BLOCK, "Thermoelectric Generator");


        //items
        addItem(ModItems.COIL_COPPER, "Copper Wire Coil");
        addItem(ModItems.COIL_IRON, "Iron Wire Coil");
        addItem(ModItems.THERMOPILE, "Iron-Copper Thermopile");
        addItem(ModItems.SOLAR_FOIL,"Solar Foil");
        addItem(ModItems.CAPSULE_EMPTY, "Space Transfer Capsule (Empty)");
        addItem(ModItems.CAPSULE_SOLAR, "Solar Satellite Space Capsule");
        addItem(ModItems.HEAT_SHIELDING, "Heat Shielding");
        addItem(ModItems.RAILGUN, "Electromagnetic Launcher");
        addItem(ModItems.STEAM_BUCKET, "Steam Bucket");

        //fluids
        add("fluid.dysonsphere.steam", "Steam");


        //guis
        add("container.dysonsphere.railgun", "Electromagnetic Orbital Launcher");
        add("container.dysonsphere.ds_energy_receiver", "Dyson Sphere Energy Receiver");
        add("container.dysonsphere.heat_generator", "Thermoelectric Generator");
        add("container.dysonsphere.heat_exchanger", "Thermal Exchanger");

        //tooltips
        add("tooltip.dysonsphere.energy_display", "%s RF / %s RF");
        add("tooltip.dysonsphere.heat_display","%s°K / %s°K");
        add("tooltip.dysonsphere.fluid_display","%s %s mB / %s mB");
        
        add("tooltip.dysonsphere.railgun_launch_energy", "Required Launch Energy: %s RF");
        add("tooltip.dysonsphere.ds_energy_receiver_energy", "To Receive:");
        add("tooltip.dysonsphere.ds_energy_receiver_nosky","Cannot see the sky!");
        add("tooltip.dysonsphere.heat_generator_neighbor_neg", "Neighbor of Negative %s");
        add("tooltip.dysonsphere.heat_generator_neighbor_pos", "Neighbor of Positive %s");
        add("tooltip.dysonsphere.heat_generator_axis", "Axis: %s");
        add("tooltip.dysonsphere.heat_generator_diff", "Diff: %s°K");
        add("tooltip.dysonsphere.heat_generator_production", "%s RF/t");
        add("tooltip.dysonsphere.heat_pipe", "Current Heat: %s°K");
        add("tooltip.dysonsphere.heat_exchanger_producing", "Producing: %s mB/t");
        add("tooltip.dysonsphere.heat_exchanger_min_heat", "Minimum Heat: %s°K");
        add("tooltip.dysonsphere.ds_monitor_status", ">Dyson Sphere Status");
        add("tooltip.dysonsphere.ds_monitor_completion","Completion: %s%%");
        add("tooltip.dysonsphere.ds_monitor_capacity","Capacity: %s RF/t");
        add("tooltip.dysonsphere.ds_monitor_parts","Parts:");
        add("tooltip.dysonsphere.ds_monitor_part", "  - %s: %sx");

        //itemGroup
        add("itemGroup.dysonsphere_tab", "Dyson Sphere Project");

        //sound - subtitles
        add("sound.dysonsphere.railgun_shot", "Electromagnetic Orbital Launcher Shot");
        add("sound.dysonsphere.railgun_charge", "Electromagnetic Orbital Launcher Charging");
        add("sound.dysonsphere.ds_energy_receiver_work", "Dyson Sphere Energy Receiver Charging");

        //recipes
        add("dysonsphere.recipe.heat_exchanger", "Heat Exchange");
        add("dysonsphere.recipe.railgun", "Orbital Launching");

        //advancements
        add("achievement.dysonsphere.start", "The Dyson Sphere Project begins!");
        add("achievement.dysonsphere.start.desc", "Start constructing the Dyson Sphere with the Electromagnetic Orbital Launcher");
        add("achievement.dysonsphere.half", "The Dyson Sphere takes Shape");
        add("achievement.dysonsphere.half.desc", "Reach 50% completion of the Dyson Sphere");
        add("achievement.dysonsphere.completed", "The Dyson Sphere is complete!");
        add("achievement.dysonsphere.completed.desc", "Complete the Dyson Sphere");
    }
    
    

}
