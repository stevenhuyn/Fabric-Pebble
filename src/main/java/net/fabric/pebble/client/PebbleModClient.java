package net.fabric.pebble.client;

import net.fabric.pebble.PebbleMod;
import net.fabric.pebble.entity.EntitySpawnPacket;
import net.fabric.pebble.networking.PebbleModNetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class PebbleModClient implements ClientModInitializer {
	public static final Identifier PacketID = new Identifier(PebbleMod.MOD_ID, "spawn_packet");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(PebbleMod.PebbleEntityType,
				(dispatcher, context) -> new FlyingItemEntityRenderer<>(dispatcher, context.getItemRenderer()));
		receiveEntityPacket();

		// We associate PLAY_PARTICLE_PACKET_ID with this callback, so the server can
		// then use that id to execute the callback.
		ClientPlayNetworking.registerGlobalReceiver(PebbleMod.PEBBLE_PACKET_ID,
				(client, handler, buf, responseSender) -> {
					// Read packet data on the event loop
					BlockPos pebblePos = buf.readBlockPos();

					client.execute(() -> {
						// Everything in this lambda is run on the render thread
						ParticleEffect particleEffect = (ParticleEffect) ParticleTypes.EXPLOSION;
						for (int i = 0; i < 8; ++i) {
							client.world.addParticle(particleEffect, pebblePos.getX(), pebblePos.getY(),
									pebblePos.getZ(), 0, 0, 0);
						}
					});
				});
	}

	public void receiveEntityPacket() {
		ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) -> {
			EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID uuid = byteBuf.readUuid();
			int entityId = byteBuf.readVarInt();
			Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
			float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			ctx.getTaskQueue().execute(() -> {
				if (MinecraftClient.getInstance().world == null)
					throw new IllegalStateException("Tried to spawn entity in a null world!");
				Entity e = et.create(MinecraftClient.getInstance().world);
				if (e == null)
					throw new IllegalStateException(
							"Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
				e.updateTrackedPosition(pos);
				e.setPos(pos.x, pos.y, pos.z);
				e.pitch = pitch;
				e.yaw = yaw;
				e.setEntityId(entityId);
				e.setUuid(uuid);
				MinecraftClient.getInstance().world.addEntity(entityId, e);
			});
		});
	}
}
