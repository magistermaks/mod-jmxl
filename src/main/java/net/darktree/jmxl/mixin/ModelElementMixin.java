package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModelElement.class)
public class ModelElementMixin implements JmxlElement {

	@Unique
	BlendMode mode = BlendMode.DEFAULT;

	@Override
	public void jmxl_setBlendMode(BlendMode mode) {
		this.mode = mode;
	}

	@Override
	public BlendMode jmxl_getBlendMode() {
		return mode;
	}

}
