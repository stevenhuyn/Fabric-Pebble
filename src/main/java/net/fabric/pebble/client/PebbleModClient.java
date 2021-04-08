package net.fabric.pebble.client;

import net.fabric.pebble.PebbleMod;
import net.fabric.pebble.entity.EntitySpawnPacket;
import net.fabric.pebble.entity.PebbleEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class PebbleModClient implements ClientModInitializer {
	public static final Identifier PEBBLE_PACKET_ID = new Identifier(PebbleMod.MOD_ID, "pebble_packet");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(PebbleMod.PebbleEntityType,
				(dispatcher, context) -> new FlyingItemEntityRenderer<>(dispatcher, context.getItemRenderer()));

		// What happens when the server speaks to client about the Pebble Entity
		ClientPlayNetworking.registerGlobalReceiver(PebbleModClient.PEBBLE_PACKET_ID,
				(client, handler, byteBuf, responseSender) -> {
					// Read packet data on the event loop
					EntityType<?> pebbleEntityType = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
					UUID uuid = byteBuf.readUuid();
					int entityId = byteBuf.readVarInt();
					Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
					float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
					float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
					Boolean isCritical = byteBuf.readBoolean();

					client.execute(() -> {
						// Everything in this lambda is run on the render thread
						if (client.world == null) {
							throw new IllegalStateException("Tried to spawn entity in a null world!");
						}

						PebbleEntity pebbleEntity = (PebbleEntity) pebbleEntityType.create(client.world);
						if (pebbleEntity == null) {
							throw new IllegalStateException("Failed to create instance of entity \""
									+ Registry.ENTITY_TYPE.getId(pebbleEntityType) + "\"!");
						}

						pebbleEntity.updateTrackedPosition(pos);
						pebbleEntity.setPos(pos.x, pos.y, pos.z);
						pebbleEntity.pitch = pitch;
						pebbleEntity.yaw = yaw;
						pebbleEntity.setEntityId(entityId);
						pebbleEntity.setUuid(uuid);
						pebbleEntity.isCritical = isCritical;
						client.world.addEntity(entityId, pebbleEntity);
					});
				});
	}
}
