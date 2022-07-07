package net.darktree.jmxl.client;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class JmxlModelElement extends ModelElement {

	public final BlendMode layer;
	public final boolean emissive;
	public final boolean no_diffuse;
	public final boolean no_ambient;

	public JmxlModelElement(Vec3f from, Vec3f to, Map<Direction, ModelElementFace> faces, @Nullable ModelRotation rotation, boolean shade, BlendMode layer, boolean emissive, boolean diffuse, boolean ambient) {
		super(from, to, faces, rotation, shade);
		this.layer = layer;
		this.emissive = emissive;
		this.no_diffuse = !diffuse;
		this.no_ambient = !ambient;
	}

}
