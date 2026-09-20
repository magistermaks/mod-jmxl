package net.darktree.jmxl.client;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class JmxlBakedModel implements BakedModel, FabricBakedModel {

	final private Sprite sprite;
	final private Mesh mesh;
	final private ModelTransformation transformation;
	final private boolean hasDepth;
	final private boolean isSideLit;
	final private boolean usesAo;
	private List<BakedQuad>[] cache = null;

	public JmxlBakedModel(Sprite sprite, Mesh mesh, ModelTransformation transformation, boolean hasDepth, boolean isSideLit, boolean usesAo) {
		// called from Unbaked Model
		this.sprite = sprite;
		this.mesh = mesh;
		this.transformation = transformation;
		this.hasDepth = hasDepth;
		this.isSideLit = isSideLit;
		this.usesAo = usesAo;
	}

	/*
	 * methods inherited from FabricBakedModel
	 */
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.meshConsumer().accept(this.mesh);
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.meshConsumer().accept(this.mesh);
	}

	/*
	 * methods inherited from BakedModel
	 */
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		if (cache == null) {
			cache = ModelHelper.toQuadLists(mesh);
		}

		if (face == null) {
			return cache[ModelHelper.NULL_FACE_ID];
		}

		return cache[face.getId()];
	}

	@Override
	public boolean useAmbientOcclusion() {
		return usesAo;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return sprite;
	}

	@Override
	public boolean hasDepth() {
		return hasDepth;
	}

	@Override
	public boolean isSideLit() {
		return isSideLit;
	}

	@Override
	public ModelTransformation getTransformation() {
		return transformation;
	}

}
