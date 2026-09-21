package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.MeshBakedGeometry;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
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
	private final static Renderer RENDERER = Objects.requireNonNull(Renderer.get(), "Fabric rendering API not loaded!");

	@Unique
	private boolean jmxl = false;

	@Unique
	private static MutableMesh getMesh() {
		return Objects.requireNonNull(RENDERER).mutableMesh();
	}

	@Override
	public void jmxl_markJmxl() {
		jmxl = true;
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

	@Unique
	private static void emitQuad(QuadEmitter emitter, BakedQuad quad, Object element, @Nullable Direction face) {
		emitter.nominalFace(face);

		if (element instanceof JmxlElement jmxl) {
			emitter.renderLayer(jmxl.jmxl_getRenderLayer());
			emitter.ambientOcclusion(jmxl.jmxl_getAmbientOcclusion());
			emitter.diffuseShade(jmxl.jmxl_getDiffuse());
		}

		emitter.fromBakedQuad(quad);
		emitter.emit();
	}

	@Inject(
			method = "bakeGeometry",
			cancellable = true,
			at = @At("HEAD")
	)
	private static void onBakeGeometry(List<ModelElement> elements, ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel model, CallbackInfoReturnable<BakedGeometry> cir) {
		if (JMXL.get()) {
			JMXL.set(false);

			MutableMesh mesh = getMesh();
			QuadEmitter emitter = mesh.emitter();

			for (ModelElement element : elements) {
				element.faces().forEach((direction, face) -> {
					Sprite sprite = baker.getSpriteGetter().get(textures, face.textureId(), model);

					BakedQuad quad = BakedQuadFactory.bake(
							baker.getVec3fInterner(),
							element.from(),
							element.to(),
							face,
							sprite,
							direction,
							settings,
							element.rotation(),
							element.shade(),
							element.lightEmission()
					);

					if (face.cullFace() == null) {
						emitQuad(emitter, quad, element, null);
						return;
					}

					Direction facing = Direction.transform(settings.getRotation().getMatrix(), face.cullFace());
					emitQuad(emitter, quad, element, facing);
				});

			}

			cir.setReturnValue(new MeshBakedGeometry(mesh.immutableCopy()));
		}
	}

}
