package net.darktree.jmxl.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(CuboidModel.Deserializer.class)
public abstract class CuboidModelDeserializerMixin {

	@Unique
	private final static String KEY = "jmxl";

	@WrapOperation(
			method = "getElements",
			at = @At(
					value = "NEW",
					args = "class=net/minecraft/client/resources/model/cuboid/UnbakedCuboidGeometry"
			)
	)
	public UnbakedCuboidGeometry deserialize(List<CuboidModelElement> elements, Operation<UnbakedCuboidGeometry> original, @Local(argsOnly = true) JsonObject json) throws JsonParseException {
		UnbakedCuboidGeometry geometry = original.call(elements);

		if (json.has(KEY) && json.get(KEY).getAsBoolean()) {
			((JmxlGeometry) (Object) geometry).jmxl_markJmxl();
		}

		return geometry;
	}

}
