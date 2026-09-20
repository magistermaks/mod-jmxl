package net.darktree.jmxl.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.client.JmxlBakedModel;
import net.darktree.jmxl.client.JmxlInitializer;
import net.darktree.jmxl.client.JmxlModelElement;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.model.SpriteGetter;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BasicBakedModel.class)
public class BasicBakedModelMixin {

	@Shadow
	private static Sprite getSprite(SpriteGetter spriteGetter, ModelTextures textures, String textureId) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static BakedQuad bake(ModelElement element, ModelElementFace face, Sprite sprite, Direction direction, ModelBakeSettings settings) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique
	private final static Renderer RENDERER;

	@Unique
	private final static MaterialFinder FINDER;

	@Unique
	private final static RenderMaterial DEFAULT;

	static {
		RENDERER = Renderer.get();
		FINDER = RENDERER.materialFinder();
		DEFAULT = FINDER.find();
	}

	@Unique
	private static MutableMesh getMesh() {
		return Objects.requireNonNull(RENDERER).mutableMesh();
	}

	@Unique
	private static RenderMaterial getMaterial(ModelElement element) {
		return (element instanceof JmxlModelElement jmxl) ? FINDER.blendMode(jmxl.layer).disableDiffuse(jmxl.no_diffuse).ambientOcclusion(jmxl.no_ambient ? TriState.FALSE : TriState.TRUE).find() : DEFAULT;
	}

	@Inject(
			method = "bake(Ljava/util/List;Lnet/minecraft/client/render/model/ModelTextures;Lnet/minecraft/client/model/SpriteGetter;Lnet/minecraft/client/render/model/ModelBakeSettings;ZZZLnet/minecraft/client/render/model/json/ModelTransformation;)Lnet/minecraft/client/render/model/BakedModel;",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/client/render/model/BasicBakedModel;getSprite(Lnet/minecraft/client/model/SpriteGetter;Lnet/minecraft/client/render/model/ModelTextures;Ljava/lang/String;)Lnet/minecraft/client/texture/Sprite;"
			),
			cancellable = true
	)
	private static void onBake(List<ModelElement> elements, ModelTextures textures, SpriteGetter spriteGetter, ModelBakeSettings settings, boolean ao, boolean isSideLit, boolean hasDepth, ModelTransformation transformation, CallbackInfoReturnable<BakedModel> cir, @Local(ordinal = 0) Sprite particle) {
		if (JmxlInitializer.IS_JMXL.get()) {
			JmxlInitializer.IS_JMXL.set(false);

			JmxlInitializer.LOGGER.info("Processing JMXL model!");

			MutableMesh mesh = getMesh();
			QuadEmitter emitter = mesh.emitter();

			for (ModelElement element : elements) {
				RenderMaterial material = getMaterial(element);

				JmxlInitializer.LOGGER.info("Using: {}!", material);

				for (Direction direction : element.faces.keySet()) {
					ModelElementFace face = element.faces.get(direction);
					Sprite sprite = getSprite(spriteGetter, textures, face.textureId());

					if (face.cullFace() == null) {
						emitter.fromVanilla(bake(element, face, sprite, direction, settings), material, null);
						continue;
					}

					emitter.fromVanilla(bake(element, face, sprite, direction, settings), material, Direction.transform(settings.getRotation().getMatrix(), face.cullFace()));
					emitter.emit();
				}

			}

			cir.setReturnValue(new JmxlBakedModel(particle, mesh.immutableCopy(), transformation, hasDepth, isSideLit, ao));
		}
	}

}
