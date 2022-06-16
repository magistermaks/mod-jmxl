package net.darktree.jmxl.mixin;

import net.darktree.jmxl.client.JmxlBakedModel;
import net.darktree.jmxl.client.JmxlModelElement;
import net.darktree.jmxl.client.JmxlUnbakedModel;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin {

	private final static Renderer RENDERER;
	private final static MeshBuilder MESH;
	private final static Map<BlendMode, RenderMaterial> MATERIALS = new IdentityHashMap<>();

	static {
		RENDERER = RendererAccess.INSTANCE.getRenderer();
		MESH = Objects.requireNonNull(RENDERER).meshBuilder();

		for (BlendMode mode : BlendMode.values()) {
			MATERIALS.put(mode, RENDERER.materialFinder().blendMode(0, mode).find());
		}
	}

	private static RenderMaterial getMaterial(ModelElement element) {
		return MATERIALS.get((element instanceof JmxlModelElement jmxl) ? jmxl.layer : BlendMode.DEFAULT);
	}

	@Inject(method="bake(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;", at=@At("HEAD"), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
	public void bake(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> info) {

		// FIXME: injecting before new BasicBakedModel Builder would be better because then the sprite could be captured and reused
		JsonUnbakedModel self = ((JsonUnbakedModel) (Object) this);
		Sprite particle = textureGetter.apply(self.resolveSprite(JsonUnbakedModel.PARTICLE_KEY));

		if (self instanceof JmxlUnbakedModel) {
			QuadEmitter emitter = MESH.getEmitter();

			for (ModelElement element : self.getElements()) {
				RenderMaterial material = getMaterial(element);

				for (Direction direction : element.faces.keySet()) {
					ModelElementFace modelElementFace = element.faces.get(direction);
					Sprite sprite = textureGetter.apply(self.resolveSprite(modelElementFace.textureId));

					if (modelElementFace.cullFace == null) {
						emitter.fromVanilla(JsonUnbakedModelMixin.createQuad(element, modelElementFace, sprite, direction, settings, id), material, null);
						emitter.emit();
						continue;
					}

					emitter.fromVanilla(JsonUnbakedModelMixin.createQuad(element, modelElementFace, sprite, direction, settings, id), material, Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace));
					emitter.emit();
				}

			}

			info.setReturnValue(new JmxlBakedModel(particle, MESH.build(), self.getTransformations(), this.compileOverrides(loader, parent), hasDepth, self.getGuiLight().isSide(), self.useAmbientOcclusion()));
		}
	}

	@Shadow
	private ModelOverrideList compileOverrides(ModelLoader loader, JsonUnbakedModel parent) {
		return null;
	}

	@Shadow
	private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
		return null;
	}

}
