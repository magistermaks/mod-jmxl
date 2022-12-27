package net.darktree.jmxl.mixin;

import com.google.gson.*;
import net.darktree.jmxl.client.JmxlModelElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.Map;

@Mixin(targets={"net.minecraft.client.render.model.json.ModelElement$Deserializer"})
public abstract class ModelElementDeserializerMixin {

	private final static String LAYER = "jmxl_layer";
	private final static String EMISSIVE = "jmxl_emissive";
	private final static String DIFFUSE = "jmxl_diffuse";
	private final static String AMBIENT = "jmxl_ambient_occlusion";
	private final static Gson GSON = new Gson();

	@Inject(method="deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelElement;", at=@At("TAIL"), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
	public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ModelElement> info, JsonObject json, Vector3f from, Vector3f to, ModelRotation rotation, Map<Direction, ModelElementFace> faces, boolean shade) throws JsonParseException {

		// technically this check is optional
		if (json.has(LAYER) || json.has(EMISSIVE) || json.has(DIFFUSE) || json.has(AMBIENT)) {

			BlendMode blend = json.has(LAYER) ? GSON.fromJson(json.get(LAYER), BlendMode.class) : BlendMode.DEFAULT;
			boolean emissive = getBoolean(json, EMISSIVE, false);
			boolean diffuse = getBoolean(json, DIFFUSE, true);
			boolean ambient = getBoolean(json, AMBIENT, true);

			info.setReturnValue(new JmxlModelElement(from, to, faces, rotation, shade, blend, emissive, diffuse, ambient));
		}
	}

	private boolean getBoolean(JsonObject object, String key, boolean def) {
		return object.has(key) ? object.get(key).getAsBoolean() : def;
	}

}
