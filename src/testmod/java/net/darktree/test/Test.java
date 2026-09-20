package net.darktree.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class Test implements ModInitializer {

	private final static Identifier ID = Identifier.of("jmxl_test", "debug");

	private final static AbstractBlock.Settings BLOCK_SETTINGS = AbstractBlock.Settings.create()
			.registryKey(RegistryKey.of(RegistryKeys.BLOCK, ID))
			.solid()
			.mapColor(MapColor.BROWN)
			.nonOpaque()
			.strength(0.3F)
			.sounds(BlockSoundGroup.GLASS);

	private final static Item.Settings ITEM_SETTINGS = new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, ID))
			.useBlockPrefixedTranslationKey();

	private final static Block TEST_BLOCK = new Block(BLOCK_SETTINGS);
	private final static Item TEST_ITEM = new BlockItem(TEST_BLOCK, ITEM_SETTINGS);

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, ID, TEST_BLOCK);
		Registry.register(Registries.ITEM, ID, TEST_ITEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
			content.add(TEST_ITEM);
		});
	}

}
