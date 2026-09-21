package net.darktree.jmxl.duck;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;

public interface JmxlElement {
	void jmxl_setBlendMode(BlendMode mode);
	BlendMode jmxl_getBlendMode();
}
