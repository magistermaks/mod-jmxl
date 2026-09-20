package net.darktree.jmxl.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin {

	@Shadow
	public abstract List<ModelOverride> getOverrides();

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

	@Unique
	private static RenderMaterial getMaterial(ModelElement element) {
		return (element instanceof JmxlModelElement jmxl) ? FINDER.blendMode(jmxl.layer).disableDiffuse(jmxl.no_diffuse).ambientOcclusion(jmxl.no_ambient ? TriState.FALSE : TriState.TRUE).find() : DEFAULT;
	}

	@Inject(
			method="bake(Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Z)Lnet/minecraft/client/render/model/BakedModel;",
			at=@At(
					value= "INVOKE_ASSIGN",
					target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;",
					ordinal = 0,
					shift = At.Shift.BY,
					by = 3
			),
			cancellable=true
	)
	public void bake(Function<SpriteIdentifier, Sprite> getter, ModelBakeSettings settings, boolean depth, CallbackInfoReturnable<BakedModel> cir, @Local Sprite particle) {

		// TODO: Change the cursed at to '@At(value="INVOKE_ASSIGN", target="Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;", ordinal=0)'
		// TODO: once a issue in mixin is fixed (https://github.com/SpongePowered/Mixin/pull/514), current workaround by LlamaLad7.

		JsonUnbakedModel self = ((JsonUnbakedModel) (Object) this);

		if (self instanceof JmxlUnbakedModel) {
			QuadEmitter emitter = MESH.getEmitter();

			for (ModelElement element : self.getElements()) {
				RenderMaterial material = getMaterial(element);

				for (Direction direction : element.faces.keySet()) {
					ModelElementFace face = element.faces.get(direction);
					Sprite sprite = getter.apply(self.resolveSprite(face.textureId()));

					if (face.cullFace() == null) {
						emitter.fromVanilla(JsonUnbakedModelMixin.createQuad(element, face, sprite, direction, settings), material, null);
						emitter.emit();
						continue;
					}

					emitter.fromVanilla(JsonUnbakedModelMixin.createQuad(element, face, sprite, direction, settings), material, Direction.transform(settings.getRotation().getMatrix(), face.cullFace()));
					emitter.emit();
				}

			}

			cir.setReturnValue(new JmxlBakedModel(particle, MESH.build(), self.getTransformations(), depth, self.getGuiLight().isSide(), self.useAmbientOcclusion()));
		}
	}

	@Shadow
	private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings) {
		throw new IllegalStateException();
	}

}
