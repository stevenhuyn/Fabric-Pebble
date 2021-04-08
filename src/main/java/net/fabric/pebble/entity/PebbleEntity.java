package net.fabric.pebble.entity;

import net.fabric.pebble.PebbleMod;
import net.fabric.pebble.client.PebbleModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PebbleEntity extends ThrownItemEntity {
    public Boolean isCritical = false;

    public PebbleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public PebbleEntity(World world, LivingEntity owner) {
        super(PebbleMod.PebbleEntityType, owner, world);
    }

    public PebbleEntity(World world, LivingEntity owner, Boolean isCritical) {
        super(PebbleMod.PebbleEntityType, owner, world);
        this.isCritical = isCritical;
    }

    public PebbleEntity(World world, double x, double y, double z) {
        super(PebbleMod.PebbleEntityType, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return PebbleMod.PebbleItem;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return EntitySpawnPacket.create(this, PebbleModClient.PEBBLE_PACKET_ID, isCritical);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
        int damage = 4;
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float) damage); // deals damage

        // checks if entity is an instance of LivingEntity (meaning it is not a boat or
        // minecart)
        if (entity instanceof LivingEntity) {
            entity.playSound(SoundEvents.BLOCK_SNOW_STEP, 2F, 1F);
        }
    }

    @Environment(EnvType.CLIENT)
    // Not entirely sure, but probably has do to with the snowball's particles.
    // (OPTIONAL)
    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (ParticleEffect) (itemStack.isEmpty() ? ParticleTypes.CRIT
                : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Environment(EnvType.CLIENT)
    // Also not entirely sure, but probably also has to do with the particles.
    // This method (as well as the previous one) are optional, so if you don't
    // understand, don't include this one.
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();
            for (int i = 0; i < 8; ++i) {
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    // called on collision with a block
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        // checks if the world is client
        if (!this.world.isClient) {
            // particle?
            this.world.sendEntityStatus(this, (byte) 3);
            this.remove(); // kills the projectile
        }
    }

    public void tick() {
        super.tick();
        if (this.world.isClient) {
            if (isCritical) {
                ParticleEffect particleEffect = (ParticleEffect) ParticleTypes.CRIT;
                for (int i = 0; i < 8; ++i) {
                    this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        }

    }
}
