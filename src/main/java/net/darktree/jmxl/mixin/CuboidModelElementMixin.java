package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CuboidModelElement.class)
public class CuboidModelElementMixin implements JmxlElement {

	@Unique
	ChunkSectionLayer layer = ChunkSectionLayer.SOLID;

	@Unique
	boolean diffuse = true;

	@Unique
	TriState ao = TriState.DEFAULT;

	@Override
	public void jmxl_setRenderLayer(ChunkSectionLayer mode) {
		this.layer = mode;
	}

	@Override
	public ChunkSectionLayer jmxl_getRenderLayer() {
		return layer;
	}

	@Override
	public void jmxl_setDiffuse(boolean diffuse) {
		this.diffuse = diffuse;
	}

	@Override
	public boolean jmxl_getDiffuse() {
		return diffuse;
	}

	@Override
	public void jmxl_setAmbientOcclusion(TriState ao) {
		this.ao = ao;
	}

	@Override
	public TriState jmxl_getAmbientOcclusion() {
		return ao;
	}

}
