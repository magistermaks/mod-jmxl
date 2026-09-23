package net.darktree.jmxl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.darktree.jmxl.client.JmxlContext;
import net.darktree.jmxl.duck.JmxlGeometry;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(UnbakedCuboidGeometry.class)
public abstract class UnbakedCuboidGeometryMixin implements JmxlGeometry {

	@Unique
	private static final ThreadLocal<Boolean> JMXL = ThreadLocal.withInitial(() -> false);

	@Unique
	private boolean jmxl = false;

	@Override
	public void jmxl_markJmxl() {
		jmxl = true;
	}

	@Inject(
			method = "bake(Lnet/minecraft/client/resources/model/sprite/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
			at = @At("HEAD")
	)
	void onModelBake(TextureSlots textures, ModelBaker modelBaker, ModelState modelState, ModelDebugName name, CallbackInfoReturnable<net.minecraft.client.resources.model.geometry.QuadCollection> cir) {
		if (jmxl) {
			JMXL.set(true);
		}
	}

	@Inject(
			method = "bake(Ljava/util/List;Lnet/minecraft/client/resources/model/sprite/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
			at = @At("HEAD")
	)
	private static void onBeginBake(List<CuboidModelElement> elements, TextureSlots textures, ModelBaker modelBaker, ModelState modelState, ModelDebugName name, CallbackInfoReturnable<net.minecraft.client.resources.model.geometry.QuadCollection> cir, @Share("jmxl") LocalRef<JmxlContext> jmxl) {
		if (JMXL.get()) {
			JMXL.set(false);
			jmxl.set(new JmxlContext());
		}
	}

	@WrapOperation(
		method = "bake(Ljava/util/List;Lnet/minecraft/client/resources/model/sprite/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
		at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/resources/model/geometry/QuadCollection$Builder;addUnculledFace(Lnet/minecraft/client/resources/model/geometry/BakedQuad;)Lnet/minecraft/client/resources/model/geometry/QuadCollection$Builder;"
		)
	)
	private static QuadCollection.Builder emitUnculledQuad(QuadCollection.Builder instance, BakedQuad quad, Operation<QuadCollection.Builder> original, @Local(name = "element") CuboidModelElement element, @Share("jmxl") LocalRef<JmxlContext> jmxl) {
		if (jmxl.get() != null) {
			jmxl.get().emitQuad(quad, quad, null);
			return instance;
		}

		return original.call(instance, quad);
	}

	@WrapOperation(
			method = "bake(Ljava/util/List;Lnet/minecraft/client/resources/model/sprite/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/resources/model/geometry/QuadCollection$Builder;addCulledFace(Lnet/minecraft/core/Direction;Lnet/minecraft/client/resources/model/geometry/BakedQuad;)Lnet/minecraft/client/resources/model/geometry/QuadCollection$Builder;"
			)
	)
	private static QuadCollection.Builder emitCulledQuad(QuadCollection.Builder instance, Direction direction, BakedQuad quad, Operation<QuadCollection.Builder> original, @Local(name = "element") CuboidModelElement element, @Share("jmxl") LocalRef<JmxlContext> jmxl) {
		if (jmxl.get() != null) {
			jmxl.get().emitQuad(quad, quad, direction);
			return instance;
		}

		return original.call(instance, direction, quad);
	}

	@Inject(
			method = "bake(Ljava/util/List;Lnet/minecraft/client/resources/model/sprite/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/ModelDebugName;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
			at = @At("TAIL"),
			cancellable = true
	)
	private static void onBakeEnd(List<CuboidModelElement> elements, TextureSlots textures, ModelBaker modelBaker, ModelState modelState, ModelDebugName name, CallbackInfoReturnable<QuadCollection> cir, @Share("jmxl") LocalRef<JmxlContext> jmxl) {
		if (jmxl.get() != null) {
			cir.setReturnValue(jmxl.get().bake());
		}
	}

}
