// package de.bax.dysonsphere.capabilities.grapplingHook;

// import net.minecraft.world.phys.Vec3;

// public interface IGrapplingHook {
    
//     public boolean isDeployed();

//     public Vec3 deployedAt();

//     public default Vec3 appliedMotion(Vec3 playerPos){
//         if(isDeployed()){
//             return playerPos.subtract(deployedAt()).normalize().scale(getForce());
//         }
//         return Vec3.ZERO;
//     }

//     public float getForce();

//     public void recall();



// }
