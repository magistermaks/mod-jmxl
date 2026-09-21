package net.darktree.jmxl.duck;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.util.TriState;

public interface JmxlElement {
	void jmxl_setBlendMode(BlendMode mode);
	BlendMode jmxl_getBlendMode();
	void jmxl_setDiffuse(boolean diffuse);
	boolean jmxl_getDiffuse();
	void jmxl_setAmbientOcclusion(TriState ao);
	TriState jmxl_getAmbientOcclusion();
}
