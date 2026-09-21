package net.darktree.jmxl.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(JsonUnbakedModel.Deserializer.class)
public abstract class JsonUnbakedModelDeserializerMixin {

	@Unique
	private final static String KEY = "jmxl";

	@WrapOperation(
			method = "elementsFromJson",
			at = @At(
					value = "NEW",
					args = "class=net/minecraft/client/render/model/UnbakedGeometry"
			)
	)
	public UnbakedGeometry deserialize(List<ModelElement> list, Operation<UnbakedGeometry> original, @Local(argsOnly = true) JsonObject json) throws JsonParseException {
		UnbakedGeometry geometry = original.call(list);

		if (json.has(KEY) && json.get(KEY).getAsBoolean()) {
			((JmxlGeometry) (Object) geometry).jmxl_markJmxl();
		}

		return geometry;
	}

}
