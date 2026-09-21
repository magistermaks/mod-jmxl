package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.render.model.json.ModelElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModelElement.class)
public class ModelElementMixin implements JmxlElement {

	@Unique
	BlendMode mode = BlendMode.DEFAULT;

	@Unique
	boolean diffuse = true;

	@Unique
	TriState ao = TriState.DEFAULT;

	@Override
	public void jmxl_setBlendMode(BlendMode mode) {
		this.mode = mode;
	}

	@Override
	public BlendMode jmxl_getBlendMode() {
		return mode;
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
