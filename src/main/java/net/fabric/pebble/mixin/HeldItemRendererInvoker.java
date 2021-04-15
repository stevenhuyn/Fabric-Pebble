package net.fabric.pebble.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HeldItemRenderer.class)
public interface HeldItemRendererInvoker {
    @Invoker("applyEquipOffset")
    public void invokeApplyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Invoker("renderArmHoldingItem")
    public void invokeRenderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);
}

