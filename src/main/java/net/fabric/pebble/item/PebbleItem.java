package net.fabric.pebble.item;

import net.fabric.pebble.entity.PebbleEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PebbleItem extends BowItem {

    public static final float baseForce = 3f;
    public static final int maxChargeTimeTicks = 22;

    public PebbleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        playerEntity.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int remainingUseTicks) {
        PlayerEntity playerEntity = (PlayerEntity) livingEntity;

        world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F); // plays a globalSoundEvent

        /*
         * user.getItemCooldownManager().set(this, 5); Optionally, you can add a
         * cooldown to your item's right-click use, similar to Ender Pearls.
         */

        if (!world.isClient) {
            int useTicks = this.getMaxUseTime(itemStack) - remainingUseTicks;
            Boolean isCritical = useTicks >= maxChargeTimeTicks;
            PebbleEntity pebbleEntity = new PebbleEntity(world, livingEntity, isCritical);
            pebbleEntity.setItem(itemStack);
            pebbleEntity.setProperties(livingEntity, livingEntity.pitch, livingEntity.yaw, 0.0f,
                    getPebblePullProgress(useTicks) * baseForce, 0f);

            // Spawns Entity Serverside with the given entity data
            // I assume this calls PebbleEntity::createSpawnPacket
            // Which will start sending the packets to the client(s?)
            world.spawnEntity(pebbleEntity);
        }

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!playerEntity.abilities.creativeMode) {
            itemStack.decrement(1); // decrements itemStack if user is not in creative mode
        }

    }

    public float getPebblePullProgress(int useTicks) {
        return useTicks > maxChargeTimeTicks ? 1f : (float) useTicks / maxChargeTimeTicks;
    }
}