package net.darktree.jmxl.mixin;

import com.google.gson.*;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.client.JmxlInitializer;
import net.darktree.jmxl.client.JmxlModelElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
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

	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelElement;", at = @At("TAIL"), cancellable = true)
	public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ModelElement> info, @Local JsonObject json, @Local(ordinal = 0) Vector3f from, @Local(ordinal = 1) Vector3f to, @Local ModelRotation rotation, @Local Map<Direction, ModelElementFace> faces, @Local boolean shade, @Local int light) throws JsonParseException {

		if (json.has(EMISSIVE)) {
			JmxlInitializer.LOGGER.error("Emissivity is a vanilla features now, replace boolean 'jmxl_emissive' with integer 'light_emission'!");
		}

		// technically this check is optional
		if (json.has(LAYER) || json.has(DIFFUSE) || json.has(AMBIENT)) {

			BlendMode blend = json.has(LAYER) ? GSON.fromJson(json.get(LAYER), BlendMode.class) : BlendMode.DEFAULT;
			boolean diffuse = getBoolean(json, DIFFUSE, true);
			boolean ambient = getBoolean(json, AMBIENT, true);

			info.setReturnValue(new JmxlModelElement(from, to, faces, rotation, shade, light, blend, diffuse, ambient));
		}
	}

	@Unique
	private boolean getBoolean(JsonObject object, String key, boolean def) {
		return object.has(key) ? object.get(key).getAsBoolean() : def;
	}

}
