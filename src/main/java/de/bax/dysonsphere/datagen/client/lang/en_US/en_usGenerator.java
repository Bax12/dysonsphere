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
        addBlock(ModBlocks.SMART_ALLOY_BLOCK, "Smart Alloy Block");
        addBlock(ModBlocks.HEAT_PIPE_BLOCK, "Thermal Coupler");
        addBlock(ModBlocks.DS_MONITOR_BLOCK, "Dyson Sphere Observer");
        addBlock(ModBlocks.RAILGUN_BLOCK, "Electromagnetic Orbital Launcher");
        addBlock(ModBlocks.DS_ENERGY_RECEIVER_BLOCK, "Dyson Sphere Energy Receiver");
        addBlock(ModBlocks.HEAT_EXCHANGER_BLOCK, "Thermal Exchanger");
        addBlock(ModBlocks.HEAT_GENERATOR_BLOCK, "Thermoelectric Generator");
        addBlock(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK, "Laser Pattern Controller Terminal");
        addBlock(ModBlocks.LASER_CONTROLLER_BLOCK, "Laser Control Station");
        addBlock(ModBlocks.LASER_CRAFTER_BLOCK, "Precision Laser Workbench");
        addBlock(ModBlocks.INPUT_HATCH_PARALLEL, "Parallel Input Hatch");
        addBlock(ModBlocks.INPUT_HATCH_PARALLEL_HEAT, "Parallel Input Hatch (heat-transfer)");
        addBlock(ModBlocks.INPUT_HATCH_SERIAL, "Serial Input Hatch");
        addBlock(ModBlocks.INPUT_HATCH_SERIAL_HEAT, "Serial Input Hatch (heat-transfer)");
        addBlock(ModBlocks.INPUT_HATCH_PROXY, "Proxy Input Hatch");
        addBlock(ModBlocks.INPUT_HATCH_PROXY_HEAT, "Proxy Input Hatch (heat-transfer)");
        addBlock(ModBlocks.INPUT_HATCH_ENERGY, "Energy Input Hatch");
        addBlock(ModBlocks.INPUT_HATCH_ENERGY_HEAT, "Energy Input Hatch (heat-transfer)");

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
        addItem(ModItems.UNIVERSE_WHISPER, "Whisper of the Universe");
        addItem(ModItems.INGOT_SMART_ALLOY, "Smart Alloy Ingot");
        addItem(ModItems.COMPONENT_SMART_ALLOY, "Smart Alloy Component");
        addItem(ModItems.CONSTRUCT_ENDER, "Stabilized Ender Construct");
        addItem(ModItems.GRAPPLING_HOOK_HARNESS, "Grappling Hook Frame");
        addItem(ModItems.GRAPPLING_HOOK_CONTROLLER, "Grappling Hook Controller");
        addItem(ModItems.GRAPPLING_HOOK_HOOK_SMART_ALLOY, "Smart Alloy Hook");
        addItem(ModItems.GRAPPLING_HOOK_HOOK_BLAZE, "Blazing Hook");
        addItem(ModItems.GRAPPLING_HOOK_HOOK_WOOD, "Wooden Hook");
        addItem(ModItems.GRAPPLING_HOOK_HOOK_SLIME, "Sticky Hook");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_STEAM, "Portable Steam Engine");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC, "Simple Electric Engine");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2, "Smart Alloy Electric Engine");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_MANUAL, "Hand Crank Engine");
        addItem(ModItems.GRAPPLING_HOOK_ROPE_ENDER, "Ender Rope");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_PRESSURE, "Pneumatic Engine");
        addItem(ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL, "Mechanical Engine");
        addItem(ModItems.WRENCH, "Wrench");

        //fluids
        add("fluid.dysonsphere.steam", "Steam");


        //guis
        add("container.dysonsphere.railgun", "Electromagnetic Orbital Launcher");
        add("container.dysonsphere.ds_energy_receiver", "Dyson Sphere Energy Receiver");
        add("container.dysonsphere.heat_generator", "Thermoelectric Generator");
        add("container.dysonsphere.heat_exchanger", "Thermal Exchanger");
        add("container.dysonsphere.laser_pattern_controller","Laser Pattern Controller Terminal");
        add("container.dysonsphere.laser_controller_inventory", "Active Laser Patterns");
        add("container.dysonsphere.laser_controller_block", "Laser Control Station");
        add("container.dysonsphere.grappling_hook_harness_inventory", "Grappling Hook Frame");
        add("container.dysonsphere.input_hatch_serial", "Serial Input Hatch");
        add("container.dysonsphere.input_hatch_serial_heat", "Serial Input Hatch (heat-transfer)");
        add("container.dysonsphere.input_hatch_parallel", "Parallel Input Hatch");
        add("container.dysonsphere.input_hatch_parallel_heat", "Parallel Input Hatch (heat-transfer)");
        add("container.dysonsphere.input_hatch_energy", "Energy Input Hatch");
        add("container.dysonsphere.input_hatch_energy_heat", "Energy Input Hatch (heat-transfer)");

        //tooltips
        add("tooltip.dysonsphere.energy_display", "%s RF / %s RF");
        add("tooltip.dysonsphere.heat_display","%s°K / %s°K");
        add("tooltip.dysonsphere.heat_current", "Current Heat: %s°K");
        add("tooltip.dysonsphere.fluid_display","%s %s mB / %s mB");
        add("tooltip.dysonsphere.number_input.plus_button","+ %s");
        add("tooltip.dysonsphere.number_input.minus_button","- %s");
        
        add("tooltip.dysonsphere.railgun_launch_energy", "Required Launch Energy: %s RF");
        add("tooltip.dysonsphere.ds_energy_receiver_energy", "To Receive:");
        add("tooltip.dysonsphere.ds_energy_receiver_nosky","Cannot see the sky!");
        add("tooltip.dysonsphere.ds_energy_receiver_wanted", "Wanted Heat Input");
        add("tooltip.dysonsphere.railgun_nosky","Cannot see the sky!");
        add("tooltip.dysonsphere.railgun_dsfull","Dyson Sphere cannot accept part!");
        add("tooltip.dysonsphere.heat_generator_neighbor_neg", "Neighbor of Negative %s");
        add("tooltip.dysonsphere.heat_generator_neighbor_pos", "Neighbor of Positive %s");
        add("tooltip.dysonsphere.heat_generator_axis", "Axis: %s");
        add("tooltip.dysonsphere.heat_generator_diff", "Diff: %s°K");
        add("tooltip.dysonsphere.heat_generator_production", "%s RF/t");
        add("tooltip.dysonsphere.heat_exchanger_producing", "Producing: %s mB/t");
        add("tooltip.dysonsphere.heat_exchanger_min_heat", "Minimum Heat: %s°K");
        add("tooltip.dysonsphere.heat_exchanger_heat_bonus", "Output +%s%% per additional %s°K");
        add("tooltip.dysonsphere.ds_monitor_status", ">Dyson Sphere Status");
        add("tooltip.dysonsphere.ds_monitor_completion","Completion: %s%%");
        add("tooltip.dysonsphere.ds_monitor_capacity","Capacity: %s RF/t");
        add("tooltip.dysonsphere.ds_monitor_parts","Parts:");
        add("tooltip.dysonsphere.ds_monitor_part", "  - %s: %sx");
        add("tooltip.dysonsphere.ds_monitor_usage", "Usage: %s%%");
        add("tooltip.dysonsphere.ds_monitor_power_draw", "Energy Draw %s RF/t");
        add("tooltip.dysonsphere.laser_pattern_call_in","Call-In Sequence");
        add("tooltip.dysonsphere.laser_pattern_name", "Name:");
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
        add("tooltip.dysonsphere.laser_pattern_controller_edit", "Edit");
        add("tooltip.dysonsphere.laser_pattern_controller_missing_energy", "Not enough energy!");
        add("tooltip.dysonsphere.laser_pattern_controller_invalid_pattern", "Pattern invalid, try extending the call-in sequence");
        add("tooltip.dysonsphere.orbital_lasers_unavailable", "Not enough lasers available!");
        add("tooltip.dysonsphere.laser_controller_launch", "Launch");
        add("tooltip.dysonsphere.laser_controller_launching", "Launching...");
        add("tooltip.dysonsphere.laser_controller_claim", "Claim");
        add("tooltip.dysonsphere.laser_controller_owner", "Owner: %s");
        add("tooltip.dysonsphere.laser_controller_no_owner", "No owner, needs to be claimed!");
        add("tooltip.dysonsphere.laser_controller_lasers", "Lasers Available: %s");
        add("tooltip.dysonsphere.laser_controller_cooldown", "On Cooldown");
        add("tooltip.dysonsphere.laser_controller_lasers_missing", "Not enough Lasers!");
        add("tooltip.dysonsphere.laser_controller_to_many", "To many Orbital Laser Controller!");
        add("tooltip.dysonsphere.laser_crafter_energy_needed", "Required Energy: %sRF");
        
        
        add("tooltip.dysonsphere.coordinates_x","X");
        add("tooltip.dysonsphere.coordinates_y","Y");
        add("tooltip.dysonsphere.coordinates_z","Z");

        add("tooltip.dysonsphere.spoiler_components", "<Press Shift for components>");
        add("tooltip.dysonsphere.spoiler_stats", "<Press Control for Grappling Hook stats>");
        

        add("tooltip.dysonsphere.orbital_laser_hud_lasers_available", "Lasers Available: %s");
        add("tooltip.dysonsphere.orbital_laser_hud_lasers_cooldown", "Lasers on Cooldown: %s");
        add("tooltip.dysonsphere.orbital_laser_hud_next_cooldown", "Next Laser: T-%s");
        add("tooltip.dysonsphere.orbital_laser_hud_pattern_name", "%s : %s");
        add("tooltip.dysonsphere.orbital_laser_hud_pattern_lasers", "Lasers: %sx %s:%s");
        add("tooltip.dysonsphere.ds_monitor_offline1","Unreachable - Offline - Critical!");
        add("tooltip.dysonsphere.ds_monitor_offline2","Maybe move closer to the sun? By a dimension or two");

        add("tooltip.dysonsphere.grappling_hook_to_many_hooks", "Cannot launch more hooks!");
        add("tooltip.dysonsphere.grappling_hook_unavailable", "Failed to launch a Grappling Hook! Check your gear");
        add("tooltip.dysonsphere.grappling_hook_hook", "Hook: %s");
        add("tooltip.dysonsphere.grappling_hook_rope", "Rope: %s");
        add("tooltip.dysonsphere.grappling_hook_engine", "Engine: %s");
        add("tooltip.dysonsphere.grappling_hook_max_distance", "Max Distance: %s");
        add("tooltip.dysonsphere.grappling_hook_max_hooks", "Hook Count: %s");
        add("tooltip.dysonsphere.grappling_hook_gravity", "Hook Gravity: %s");
        add("tooltip.dysonsphere.grappling_hook_launch_force", "Launch Force: %s");
        add("tooltip.dysonsphere.grappling_hook_winch_force", "Winch Force: %s");
        add("tooltip.dysonsphere.grappling_hook_gravity_mult", "Hook Gravity Multiplier: %s");
        add("tooltip.dysonsphere.grappling_hook_launch_mult", "Launch Force Multiplier: %s");
        add("tooltip.dysonsphere.grappling_hook_winch_mult", "Winch Force Multiplier: %s");

        add("tooltip.dysonsphere.grappling_hook_engine_mechanical.desc", "Allows for limited free movement");

        add("dysonsphere.tooltip.pneumaticcraft.pressure", "Pressure: %s/%s bar");

        add("tooltip.dysonsphere.grappling_hook_status", "GrapplingHook Status: %s");
        add("tooltip.dysonsphere.grappling_hook_status_pull", "Pulling");
        add("tooltip.dysonsphere.grappling_hook_status_stop", "Stopped");
        add("tooltip.dysonsphere.grappling_hook_status_unwind", "Unwinding");
        add("tooltip.dysonsphere.grappling_hook_deployed", "GrapplingHooks Deployed: %s/%s");

        add("tooltip.dysonsphere.laser_controller_target", "Aiming at: X: %s Y: %s Z: %s");

        //itemGroup
        add("itemGroup.dysonsphere_tab", "Dyson Sphere Project");

        //sound - subtitles
        add("sound.dysonsphere.railgun_shot", "Electromagnetic Orbital Launcher Shot");
        add("sound.dysonsphere.railgun_charge", "Electromagnetic Orbital Launcher Charging");
        add("sound.dysonsphere.ds_energy_receiver_work", "Dyson Sphere Energy Receiver Charging");
        add("sound.dysonsphere.electric_winch", "Electric Winch Running");

        //recipes
        add("dysonsphere.recipe.heat_exchanger", "Heat Exchange");
        add("dysonsphere.recipe.railgun", "Orbital Launching");
        add("dysonsphere.recipe.laser", "Orbital Laser Strike");

        //advancements
        add("achievement.dysonsphere.root", "Dyson Sphere Project");
        add("achievement.dysonsphere.root.desc", "The Project begins!");
        add("achievement.dysonsphere.start", "The first step of many!");
        add("achievement.dysonsphere.start.desc", "Start constructing the Dyson Sphere with the Electromagnetic Orbital Launcher");
        add("achievement.dysonsphere.half", "The Dyson Sphere takes Shape");
        add("achievement.dysonsphere.half.desc", "Reach 50% completion of the Dyson Sphere");
        add("achievement.dysonsphere.completed", "The Dyson Sphere is complete!");
        add("achievement.dysonsphere.completed.desc", "Complete the Dyson Sphere");
        add("achievement.dysonsphere.laser_strike", "Ouranophobia");
        add("achievement.dysonsphere.laser_strike.desc", "Call down an Orbital Laser Strike. Fear is natural");
        add("achievement.dysonsphere.praise", "Praise be!");
        add("achievement.dysonsphere.praise.desc", "Show your devotion with a special Orbital Laser");
        add("achievement.dysonsphere.get_hook_harness", "Gear up!");
        add("achievement.dysonsphere.get_hook_harness.desc", "Get yourself a Grappling Hook Harness. And maybe fill it.");
        add("achievement.dysonsphere.hook_detach", "EPAH!");
        add("achievement.dysonsphere.hook_detach.desc", "Experience the consequences of overextending a rope");
        add("achievement.dysonsphere.hook_speed_50", "Mass and Momentum");
        add("achievement.dysonsphere.hook_speed_50.desc", "Experience an Acceleration of over 50 blocks per second from a grappling hook.");
        add("achievement.dysonsphere.hook_hanging", "Just hanging around");
        add("achievement.dysonsphere.hook_hanging.desc", "Hang idle in your grappling hook");

        //controls - keys
        add("key.dysonsphere.orbital_laser", "Dyson Sphere - Orbital Lasers");
        add("key.dysonsphere.orbital_laser_control", "Orbital Laser Menu Key");
        add("key.dysonsphere.orbital_laser_seq_down", "Input Sequence: Down");
        add("key.dysonsphere.orbital_laser_seq_left", "Input Sequence: Left");
        add("key.dysonsphere.orbital_laser_seq_right", "Input Sequence: Right");
        add("key.dysonsphere.orbital_laser_seq_up", "Input Sequence: Up");

        add("key.dysonsphere.grappling_hook", "Dyson Sphere - Grappling Hook");
        add("key.dysonsphere.grappling_hook_deploy", "Launch Grappling Hook");
        add("key.dysonsphere.grappling_hook_recall", "Recall Grappling Hook(s)");
        add("key.dysonsphere.grappling_hook_pull", "Toggle Winch: Pulling");
        add("key.dysonsphere.grappling_hook_unwind", "Toggle Winch: Unwinding");


        //jade aka waila
        add("config.jade.plugin_dysonsphere.heat", "heat");
        add("config.jade.plugin_dysonsphere.ds_energy_receiver", "ds_receiver_target_rate");
        add("config.jade.plugin_dysonsphere.heat_generator", "heat_generator");
        add("config.jade.plugin_dysonsphere.laser_controller", "laser_controller");

        //commands
        add("commands.dysonsphere.list", "Dyson Sphere Parts:");
        add("commands.dysonsphere.add_success", "Added %sx %s to the Dyson Sphere");
        add("commands.dysonsphere.add_failure", "Failed to add %s. Can it fit into the Dyson Sphere?");
        add("commands.dysonsphere.remove_success", "Removed %sx %s from the Dyson Sphere");
        add("commands.dysonsphere.remove_failure", "Failed to remove %s. Is it part of the Dyson Sphere?");
        add("commands.dysonsphere.set_success", "Set %s count to %s (or maximum)");
        add("commands.dysonsphere.set_failure", "Failed to set %s count. Does it belong in a Dyson Sphere?");
    }
    
    

}
