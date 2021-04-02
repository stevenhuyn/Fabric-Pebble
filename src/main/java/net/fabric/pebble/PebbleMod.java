package net.fabric.pebble;

import net.fabric.pebble.entity.PebbleEntity;
import net.fabric.pebble.item.PebbleItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PebbleMod implements ModInitializer {

	public static final String MOD_ID = "fabric_pebble_mod";

	public static final Item PebbleItem = new PebbleItem(new FabricItemSettings().group(ItemGroup.COMBAT));
	public static final EntityType<PebbleEntity> PebbleEntityType = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(MOD_ID, "pebble"),
			FabricEntityTypeBuilder.<PebbleEntity>create(SpawnGroup.MISC, PebbleEntity::new)
					.dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
					.trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents
																// it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pebble"), PebbleItem);

		System.out.println("Hello Fabric world!");
	}
}
