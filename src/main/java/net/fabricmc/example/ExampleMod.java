package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ExampleMod implements ModInitializer {

	public static final FabricPebble FABRIC_PEBBLE = new FabricPebble(new FabricItemSettings().group(ItemGroup.COMBAT));

	/*
	 * Registers our Cube Entity under the ID "entitytesting:cube".
	 *
	 * The entity is registered under the SpawnGroup#CREATURE category, which is
	 * what most animals and passive/neutral mobs use. It has a hitbox size of
	 * .75x.75, or 12 "pixels" wide (3/4ths of a block).
	 */
	public static final EntityType<CubeEntity> CUBE = Registry.register(Registry.ENTITY_TYPE,
			new Identifier("tutorial", "cube"), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CubeEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registry.ITEM, new Identifier("tutorial", "fabric_pebble"), FABRIC_PEBBLE);
		FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createMobAttributes());

		System.out.println("Hello Fabric world!");

		/*
		 * Registers our Cube Entity's renderer, which provides a model and texture for
		 * the entity.
		 *
		 * Entity Renderers can also manipulate the model before it renders based on
		 * entity context (EndermanEntityRenderer#render).
		 */
		EntityRendererRegistry.INSTANCE.register(ExampleMod.CUBE, (dispatcher, context) -> {
			return new CubeEntityRenderer(dispatcher);
		});
	}
}
