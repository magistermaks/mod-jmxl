package net.darktree.jmxl.client;

import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.MeshQuadCollection;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class JmxlContext {

	private final static Renderer RENDERER = Objects.requireNonNull(Renderer.get(), "Fabric rendering API not loaded!");

	private final MutableMesh mesh;
	private final QuadEmitter emitter;

	public JmxlContext() {
		this.mesh = RENDERER.mutableMesh();
		this.emitter = mesh.emitter();
	}

	public void emitQuad(BakedQuad quad, Object element, @Nullable Direction face) {
		emitter.nominalFace(face);

		if (element instanceof JmxlElement jmxl) {
			emitter.chunkLayer(jmxl.jmxl_getRenderLayer());
			emitter.ambientOcclusion(jmxl.jmxl_getAmbientOcclusion());
			emitter.diffuseShade(jmxl.jmxl_getDiffuse());
		}

		emitter.fromBakedQuad(quad);
		emitter.emit();
	}

	public MeshQuadCollection bake() {
		return new MeshQuadCollection(mesh.immutableCopy());
	}

}
