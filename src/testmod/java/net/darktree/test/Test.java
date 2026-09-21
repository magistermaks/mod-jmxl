package net.darktree.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class Test implements ModInitializer {

	private final static Identifier ID = Identifier.fromNamespaceAndPath("jmxl_test", "debug");

	private final static BlockBehaviour.Properties BLOCK_SETTINGS = BlockBehaviour.Properties.of()
			.setId(ResourceKey.create(Registries.BLOCK, ID))
			.forceSolidOn()
			.mapColor(MapColor.COLOR_BROWN)
			.noOcclusion()
			.strength(0.3F)
			.sound(SoundType.GLASS);

	private final static Item.Properties ITEM_SETTINGS = new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, ID))
			.useBlockDescriptionPrefix();

	private final static Block TEST_BLOCK = new Block(BLOCK_SETTINGS);
	private final static Item TEST_ITEM = new BlockItem(TEST_BLOCK, ITEM_SETTINGS);

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.BLOCK, ID, TEST_BLOCK);
		Registry.register(BuiltInRegistries.ITEM, ID, TEST_ITEM);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
			content.accept(TEST_ITEM);
		});
	}

}
