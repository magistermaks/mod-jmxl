package net.darktree.jmxl.mixin;

import net.darktree.jmxl.client.JmxlBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ItemRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderContext.class)
public abstract class IndigoItemRenderContextMixin {

	@Shadow
	private ModelTransformation.Mode transformMode;

	@Inject(method="renderModel", at=@At(value="INVOKE", target="Lnet/fabricmc/fabric/impl/client/indigo/renderer/render/ItemRenderContext;selectVertexConsumer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
	private void renderModel(ItemStack itemStack, ModelTransformation.Mode transformMode, boolean invert, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int lightmap, int overlay, FabricBakedModel model, ItemRenderContext.VanillaQuadHandler vanillaHandler, CallbackInfo info) {
		if (model instanceof JmxlBakedModel) {
			this.transformMode = ModelTransformation.Mode.GROUND;
		}
	}

}
