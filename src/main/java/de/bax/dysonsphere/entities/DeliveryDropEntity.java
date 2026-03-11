package de.bax.dysonsphere.entities;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.aaaparticle.AAAParticle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

//bare-bones for cargo delivery visual.
public class DeliveryDropEntity extends Projectile implements IEntityAdditionalSpawnData {

    protected int lifetime = 0;
    protected int end = 200;
    protected int targetY;

    public DeliveryDropEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public DeliveryDropEntity(Level level){
        this(ModEntities.DELIVERY_DROP.get(), level);
    }

    @Override
    public void tick() {
        super.tick();

        if(lifetime >= getLifeTime() || this.getY() <= targetY){
            this.discard();
        }
        this.setPos(this.getX(), Math.max(this.getY() - 20f, targetY), this.getZ());

        lifetime++;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if(ModCompat.isLoaded(ModCompat.MODID.AAA_PARTICLE)){
            AAAParticle.bindPlasmaEmitter(this);
        }
    }

    public int getLifeTime(){
        return end;
    }

    public DeliveryDropEntity setTargetY(int targetY){
        this.targetY = targetY;
        return this;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(lifetime);
        buffer.writeInt(targetY);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        lifetime = buffer.readInt();
        targetY = buffer.readInt();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        lifetime = tag.getInt("lifetime");
        targetY = tag.getInt("target");
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        tag.putInt("lifetime", lifetime);
        tag.putInt("target", targetY);
    }
    
}
