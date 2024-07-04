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
        addBlock(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK, "Laser Pattern Controller Terminal");


        //items
        addItem(ModItems.COIL_COPPER, "Copper Wire Coil");
        addItem(ModItems.COIL_IRON, "Iron Wire Coil");
        addItem(ModItems.THERMOPILE, "Iron-Copper Thermopile");
        addItem(ModItems.SOLAR_FOIL,"Solar Foil");
        addItem(ModItems.CAPSULE_EMPTY, "Space Transfer Capsule (Empty)");
        addItem(ModItems.CAPSULE_SOLAR, "Solar Satellite Space Capsule");
        addItem(ModItems.CAPSULE_LASER, "Laser Satellite Space Capsule");
        addItem(ModItems.HEAT_SHIELDING, "Heat Shielding");
        addItem(ModItems.RAILGUN, "Electromagnetic Launcher");
        addItem(ModItems.STEAM_BUCKET, "Steam Bucket");
        addItem(ModItems.LASER_CONTROLLER, "Orbital Uplink Bracelet");
        addItem(ModItems.TARGET_DESIGNATOR, "Target Designator");
        addItem(ModItems.LASER_PATTERN, "Orbital Laser Pattern");

        //fluids
        add("fluid.dysonsphere.steam", "Steam");


        //guis
        add("container.dysonsphere.railgun", "Electromagnetic Orbital Launcher");
        add("container.dysonsphere.ds_energy_receiver", "Dyson Sphere Energy Receiver");
        add("container.dysonsphere.heat_generator", "Thermoelectric Generator");
        add("container.dysonsphere.heat_exchanger", "Thermal Exchanger");
        add("container.dysonsphere.laser_pattern_controller","Laser Pattern Controller Terminal");
        add("container.dysonsphere.laser_controller_inventory", "Active Laser Patterns");

        //tooltips
        add("tooltip.dysonsphere.energy_display", "%s RF / %s RF");
        add("tooltip.dysonsphere.heat_display","%s°K / %s°K");
        add("tooltip.dysonsphere.fluid_display","%s %s mB / %s mB");
        add("tooltip.dysonsphere.number_input.plus_button","+ %s");
        add("tooltip.dysonsphere.number_input.minus_button","- %s");
        
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
        add("tooltip.dysonsphere.laser_pattern_call_in","Call-In Sequence: %s");
        add("tooltip.dysonsphere.laser_pattern_controller_apply","Apply");
        add("tooltip.dysonsphere.laser_pattern_controller_count","Count");
        add("tooltip.dysonsphere.laser_pattern_controller_size","Size");
        add("tooltip.dysonsphere.laser_pattern_controller_duration","Duration");
        add("tooltip.dysonsphere.laser_pattern_controller_aim_area","Aim Area");
        add("tooltip.dysonsphere.laser_pattern_controller_homing_area","Homing Area");
        add("tooltip.dysonsphere.laser_pattern_controller_homing_speed","Homing Speed");
        add("tooltip.dysonsphere.laser_pattern_controller_damage","Damage");
        add("tooltip.dysonsphere.laser_pattern_controller_block_damage","Block Damage");
        add("tooltip.dysonsphere.laser_pattern_controller_start_delay","Call-In Delay");
        add("tooltip.dysonsphere.laser_pattern_controller_repeat_delay","Wave Delay");
        add("tooltip.dysonsphere.laser_pattern_controller_spread","Inaccuracy");
        add("tooltip.dysonsphere.laser_pattern_controller_min_sequence","Minimum Sequence Length: %s");

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
