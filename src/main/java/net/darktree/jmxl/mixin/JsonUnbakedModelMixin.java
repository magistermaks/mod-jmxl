package net.darktree.jmxl.mixin;

import net.darktree.jmxl.client.JmxlInitializer;
import net.darktree.jmxl.client.JmxlUnbakedModel;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin {

	@Inject(
			method = "bake",
			at = @At("HEAD")
	)
	public void bake(ModelTextures textures, Baker baker, ModelBakeSettings settings, boolean ambientOcclusion, boolean isSideLit, ModelTransformation transformation, CallbackInfoReturnable<BakedModel> cir) {
		if ((Object) this instanceof JmxlUnbakedModel jmxl) {
			JmxlInitializer.IS_JMXL.set(true);
			JmxlInitializer.LOGGER.info("Found JMXL model!");
		}
	}

}
