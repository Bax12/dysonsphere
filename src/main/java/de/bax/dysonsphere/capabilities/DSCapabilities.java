package de.bax.dysonsphere.capabilities;

import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookContainer;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookFrame;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookHook;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookRope;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserPatternContainer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class DSCapabilities {
    
    //TileEntity
    public static final Capability<IHeatContainer> HEAT = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IDSEnergyReceiver> DS_ENERGY_RECEIVER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ILaserReceiver> LASER_RECEIVER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IInputProvider> INPUT_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IInputAcceptor> INPUT_ACCEPTOR = CapabilityManager.get(new CapabilityToken<>() {});

    //Items
    public static final Capability<IDSPart> DS_PART = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IOrbitalLaserPatternContainer> ORBITAL_LASER_PATTERN_CONTAINER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IGrapplingHookFrame> GRAPPLING_HOOK_FRAME = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IGrapplingHookEngine> GRAPPLING_HOOK_ENGINE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IGrapplingHookRope> GRAPPLING_HOOK_ROPE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IGrapplingHookHook> GRAPPLING_HOOK_HOOK = CapabilityManager.get(new CapabilityToken<>(){});

    //Level
    public static final Capability<IDysonSphereContainer> DYSON_SPHERE = CapabilityManager.get(new CapabilityToken<>(){});

    //Players
    public static final Capability<IOrbitalLaserContainer> ORBITAL_LASER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IGrapplingHookContainer> GRAPPLING_HOOK_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});
}
