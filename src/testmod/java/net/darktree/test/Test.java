package net.darktree.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class Test implements ModInitializer {

	public final static Identifier ID = new Identifier("jmxl_test", "debug");
	public final static Block TEST_BLOCK = new Block(FabricBlockSettings.of(Material.REDSTONE_LAMP).nonOpaque().strength(0.3F).sounds(BlockSoundGroup.GLASS));
	public final static Item TEST_ITEM = new BlockItem(TEST_BLOCK, new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.BLOCK, ID, TEST_BLOCK);
		Registry.register(Registries.ITEM, ID, TEST_ITEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
			content.add(TEST_ITEM);
		});
	}

}
