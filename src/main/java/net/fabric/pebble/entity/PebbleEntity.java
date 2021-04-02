package net.fabric.pebble.entity;

import net.fabric.pebble.PebbleMod;
import net.fabric.pebble.client.PebbleModClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class PebbleEntity extends ThrownItemEntity {
    public PebbleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public PebbleEntity(World world, LivingEntity owner) {
        super(PebbleMod.PebbleEntityType, owner, world);
    }

    public PebbleEntity(World world, double x, double y, double z) {
        super(PebbleMod.PebbleEntityType, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return PebbleMod.PebbleItem;
    }

    @Override
    public Packet createSpawnPacket() {
        return EntitySpawnPacket.create(this, PebbleModClient.PacketID);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
        int i = entity instanceof BlazeEntity ? 3 : 0; // sets i to 3 if the Entity instance is an instance of
                                                       // BlazeEntity
        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float) i); // deals damage

        // checks if entity is an instance of LivingEntity (meaning it is not a boat or
        // minecart)
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 3, 0)));
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 3, 2)));
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.POISON, 20 * 3, 1)));
            entity.playSound(SoundEvents.BLOCK_SNOW_STEP, 2F, 1F);
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
}
