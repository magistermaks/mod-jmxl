package net.darktree.jmxl.duck;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public interface JmxlElement {
	void jmxl_setRenderLayer(ChunkSectionLayer layer);
	ChunkSectionLayer jmxl_getRenderLayer();
	void jmxl_setDiffuse(boolean diffuse);
	boolean jmxl_getDiffuse();
	void jmxl_setAmbientOcclusion(TriState ao);
	TriState jmxl_getAmbientOcclusion();
}
