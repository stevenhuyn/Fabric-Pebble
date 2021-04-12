package net.fabric.pebble.mixin;

import net.fabric.pebble.PebbleMod;
import net.fabric.pebble.item.PebbleItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    // Private method, so can't shadow it
    //	@Shadow abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);


    @Inject(at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"), method = "renderFirstPersonItem", cancellable = true)
    private void init(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {

        // Code below largely collected from HeldItemRenderer.renderFirstPersonItem
        boolean isMainHand = hand == Hand.MAIN_HAND;
        Arm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        matrices.push();
        if (item.getItem() == PebbleMod.PebbleItem) {
            // I'm sorry
            float x, y, z, w, u, v;
            int o = isMainHand ? 1 : -1;

            ((HeldItemRendererInvoker) this).invokeApplyEquipOffset(matrices, arm, equipProgress);
            
            int itemUseTimeLeft = this.client.player.getItemUseTimeLeft();
            u = itemUseTimeLeft != 0 ? (float) item.getMaxUseTime() - ((float) itemUseTimeLeft - tickDelta + 1.0F) : item.getMaxUseTime();

            matrices.translate((double) ((float) o * -0.2785682F), 0.18344387412071228D, 0.15731531381607056D);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935F));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) o * 35.3F));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) o * -9.785F));

            v = u / (float) PebbleItem.maxChargeTimeTicks;
            v = (v * v + v * 2.0F) / 3.0F;
            if (v > 1.0F) {
                v = 1.0F;
            }

            if (v > 0.1F) {
                w = MathHelper.sin((u - 0.1F) * 1.3F);
                x = v - 0.1F;
                y = w * x;
                matrices.translate((double) (y * 0.0F), (double) (y * 0.004F), (double) (y * 0.0F));
            }

            matrices.translate((double) (v * 0.0F), (double) (v * 0.0F), (double) (v * 0.04F));
            matrices.scale(1.0F, 1.0F, 1.0F + v * 0.2F);
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float) o * 45.0F));
            this.renderItem(player, item, isMainHand ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !isMainHand, matrices, vertexConsumers, light);

            matrices.pop();
            info.cancel();
        }
    }
}
