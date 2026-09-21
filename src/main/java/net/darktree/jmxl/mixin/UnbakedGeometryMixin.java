package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.MeshBakedGeometry;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
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

@Mixin(UnbakedGeometry.class)
public abstract class UnbakedGeometryMixin implements JmxlGeometry {

	@Unique
	private static final ThreadLocal<Boolean> JMXL = ThreadLocal.withInitial(() -> false);

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
	private boolean jmxl = false;

	@Shadow
	private static BakedQuad bakeQuad(ModelElement element, ModelElementFace face, Sprite sprite, Direction facing, ModelBakeSettings settings) {
		throw new UnsupportedOperationException();
	}

	@Unique
	private static MutableMesh getMesh() {
		return Objects.requireNonNull(RENDERER).mutableMesh();
	}

	@Override
	public void jmxl_markJmxl() {
		jmxl = true;
	}

	@Unique
	private static RenderMaterial getMaterial(Object element) {
		if (element instanceof JmxlElement jmxl) {
			return FINDER
					.blendMode(jmxl.jmxl_getBlendMode())
					.disableDiffuse(!jmxl.jmxl_getDiffuse())
					.ambientOcclusion(jmxl.jmxl_getAmbientOcclusion())
					.find();
		}

		return DEFAULT;
	}

	@Inject(
			method = "bake",
			at = @At("HEAD")
	)
	void onModelBake(ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel simpleModel, CallbackInfoReturnable<BakedGeometry> cir) {
		if (jmxl) {
			JMXL.set(true);
		}
	}

	@Inject(
			method = "bakeGeometry",
			cancellable = true,
			at = @At("HEAD")
	)
	private static void onBakeGeometry(List<ModelElement> elements, ModelTextures textures, ErrorCollectingSpriteGetter sprites, ModelBakeSettings settings, SimpleModel model, CallbackInfoReturnable<BakedGeometry> cir) {
		if (JMXL.get()) {
			JMXL.set(false);

			MutableMesh mesh = getMesh();
			QuadEmitter emitter = mesh.emitter();

			for (ModelElement element : elements) {
				RenderMaterial material = getMaterial(element);

				element.faces().forEach((direction, face) -> {
					Sprite sprite = sprites.get(textures, face.textureId(), model);

					if (face.cullFace() == null) {
						emitter.fromVanilla(bakeQuad(element, face, sprite, direction, settings), material, null);
						emitter.emit();
						return;
					}

					emitter.fromVanilla(bakeQuad(element, face, sprite, direction, settings), material, Direction.transform(settings.getRotation().getMatrix(), face.cullFace()));
					emitter.emit();
				});

			}

			cir.setReturnValue(new MeshBakedGeometry(mesh.immutableCopy()));
		}
	}

}
