package net.darktree.jmxl.mixin;

import com.google.gson.*;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.client.JmxlInitializer;
import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(targets={"net.minecraft.client.render.model.json.ModelElement$Deserializer"})
public abstract class ModelElementDeserializerMixin {

	@Unique
	private final static String LAYER = "jmxl_layer";

	@Unique
	private final static String EMISSIVE = "jmxl_emissive";

	@Unique
	private final static String DIFFUSE = "jmxl_diffuse";

	@Unique
	private final static String AMBIENT = "jmxl_ambient_occlusion";

	@Unique
	private final static Gson GSON = new Gson();

	@WrapOperation(
			method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelElement;",
			at = @At(
					value = "NEW",
					args = "class=net/minecraft/client/render/model/json/ModelElement"
			)
	)
	public ModelElement deserialize(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces, ModelRotation rotation, boolean shade, int light, Operation<ModelElement> original, @Local(ordinal = 0) JsonObject json) throws JsonParseException {
		ModelElement element = original.call(from, to, faces, rotation, shade, light);

		if (json.has(EMISSIVE)) {
			JmxlInitializer.LOGGER.error("Emissivity is a vanilla features now, replace boolean 'jmxl_emissive' with integer 'light_emission'!");
		}

		// technically this check is optional
		if (json.has(LAYER) || json.has(DIFFUSE) || json.has(AMBIENT)) {

			BlendMode blend = json.has(LAYER) ? GSON.fromJson(json.get(LAYER), BlendMode.class) : BlendMode.DEFAULT;
			boolean diffuse = getBoolean(json, DIFFUSE, true); // TODO
			boolean ambient = getBoolean(json, AMBIENT, true); // TODO

			JmxlElement jmxl = ((JmxlElement) (Object) element);
			jmxl.jmxl_setBlendMode(blend);
		}

		return element;
	}

	@Unique
	private boolean getBoolean(JsonObject object, String key, boolean def) {
		return object.has(key) ? object.get(key).getAsBoolean() : def;
	}

}
