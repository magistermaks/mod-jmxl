package net.darktree.jmxl.mixin;

import net.darktree.jmxl.duck.JmxlElement;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.MeshBakedGeometry;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(SimpleUnbakedGeometry.class)
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
			method = "bake(Lnet/minecraft/client/renderer/block/model/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/QuadCollection;",
			at = @At("HEAD")
	)
	void onModelBake(TextureSlots textures, ModelBaker baker, ModelState settings, ModelDebugName simpleModel, CallbackInfoReturnable<QuadCollection> cir) {
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
			method = "bake(Ljava/util/List;Lnet/minecraft/client/renderer/block/model/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/QuadCollection;",
			cancellable = true,
			at = @At("HEAD")
	)
	private static void onBakeGeometry(List<BlockElement> elements, TextureSlots textures, ModelBaker baker, ModelState settings, ModelDebugName model, CallbackInfoReturnable<QuadCollection> cir) {
		if (JMXL.get()) {
			JMXL.set(false);

			MutableMesh mesh = getMesh();
			QuadEmitter emitter = mesh.emitter();

			for (BlockElement element : elements) {
				element.faces().forEach((direction, face) -> {
					TextureAtlasSprite sprite = baker.sprites().resolveSlot(textures, face.texture(), model);

					BakedQuad quad = FaceBakery.bakeQuad(
							baker.parts(),
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

					if (face.cullForDirection() == null) {
						emitQuad(emitter, quad, element, null);
						return;
					}

					Direction facing = Direction.rotate(settings.transformation().getMatrix(), face.cullForDirection());
					emitQuad(emitter, quad, element, facing);
				});

			}

			cir.setReturnValue(new MeshBakedGeometry(mesh.immutableCopy()));
		}
	}

}
