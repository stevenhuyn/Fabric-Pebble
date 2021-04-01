package net.fabricmc.example;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FabricPebble extends BowItem {

    public static float baseForce = 10f;
    public static float chargeTime = 10f;

    public FabricPebble(Settings settings) {
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
        ArrowItem arrowItem = (ArrowItem) ((ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem()
                : Items.ARROW));

        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, livingEntity);

        persistentProjectileEntity.setCustomName(new LiteralText("Pebble"));

        persistentProjectileEntity.setProperties(livingEntity, livingEntity.pitch, livingEntity.yaw, 0.0f, 1.0f, 1.0f);

        world.spawnEntity(persistentProjectileEntity);
    }
}