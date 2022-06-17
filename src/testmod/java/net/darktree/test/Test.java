package net.darktree.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Test implements ModInitializer {

	public final static Block TEST_BLOCK = new Block(FabricBlockSettings.of(Material.REDSTONE_LAMP).nonOpaque().strength(0.3F).sounds(BlockSoundGroup.GLASS));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("jmxl_test", "debug"), TEST_BLOCK);
	}

}
