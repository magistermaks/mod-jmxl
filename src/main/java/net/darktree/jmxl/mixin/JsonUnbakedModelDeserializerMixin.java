package net.darktree.jmxl.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.duck.JmxlGeometry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BlockModel.Deserializer.class)
public abstract class JsonUnbakedModelDeserializerMixin {

	@Unique
	private final static String KEY = "jmxl";

	@WrapOperation(
			method = "getElements",
			at = @At(
					value = "NEW",
					args = "class=net/minecraft/client/renderer/block/model/SimpleUnbakedGeometry"
			)
	)
	public SimpleUnbakedGeometry deserialize(List<BlockElement> list, Operation<SimpleUnbakedGeometry> original, @Local(argsOnly = true) JsonObject json) throws JsonParseException {
		SimpleUnbakedGeometry geometry = original.call(list);

		if (json.has(KEY) && json.get(KEY).getAsBoolean()) {
			((JmxlGeometry) (Object) geometry).jmxl_markJmxl();
		}

		return geometry;
	}

}
