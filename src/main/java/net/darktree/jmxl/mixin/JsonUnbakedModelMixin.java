package net.darktree.jmxl.mixin;

import net.darktree.jmxl.client.JmxlBakedModel;
import net.darktree.jmxl.client.JmxlModelElement;
import net.darktree.jmxl.client.JmxlUnbakedModel;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.*;
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
import org.spongepowered.asm.mixin.Unique;
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

	@Unique
	private final static Renderer RENDERER;

	@Unique
	private final static MeshBuilder MESH;

	@Unique
	private final static MaterialFinder FINDER;

	@Unique
	private final static RenderMaterial DEFAULT;

	static {
		RENDERER = RendererAccess.INSTANCE.getRenderer();
		MESH = Objects.requireNonNull(RENDERER).meshBuilder();
		FINDER = RENDERER.materialFinder();
		DEFAULT = FINDER.find();
	}

	private static RenderMaterial getMaterial(ModelElement element) {
		return (element instanceof JmxlModelElement jmxl) ? FINDER.blendMode(0, jmxl.layer).emissive(0, jmxl.emissive).disableDiffuse(0, jmxl.no_diffuse).disableAo(0, jmxl.no_ambient).find() : DEFAULT;
	}

	@Inject(method="bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;", at=@At(value="INVOKE", target="Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;", ordinal=0, shift=At.Shift.BY, by=3), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
	public void bake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth, CallbackInfoReturnable<BakedModel> info, Sprite particle) {

		// TODO: Change the cursed at to '@At(value="INVOKE_ASSIGN", target="Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;", ordinal=0)'
		// TODO: once a issue in mixin is fixed (https://github.com/SpongePowered/Mixin/pull/514), current workaround by LlamaLad7.

		JsonUnbakedModel self = ((JsonUnbakedModel) (Object) this);

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

			info.setReturnValue(new JmxlBakedModel(particle, MESH.build(), self.getTransformations(), this.compileOverrides(baker, parent), hasDepth, self.getGuiLight().isSide(), self.useAmbientOcclusion()));
		}
	}

	@Shadow
	private ModelOverrideList compileOverrides(Baker baker, JsonUnbakedModel parent) {
		throw new IllegalStateException();
	}

	@Shadow
	private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
		throw new IllegalStateException();
	}

}
