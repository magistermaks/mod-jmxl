package net.darktree.jmxl.mixin;

import com.google.gson.*;
import net.darktree.jmxl.client.JmxlModelElement;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.Map;

@Mixin(targets={"net.minecraft.client.render.model.json.ModelElement$Deserializer"})
public abstract class ModelElementDeserializerMixin {

	private final static String KEY = "jmxl_layer";
	private final static Gson GSON = new Gson();

	@Inject(method="deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/ModelElement;", at=@At("TAIL"), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
	public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<ModelElement> info, JsonObject jsonObject, Vec3f from, Vec3f to, ModelRotation rotation, Map<Direction, ModelElementFace> faces, boolean shade) throws JsonParseException {
		JsonObject object = jsonElement.getAsJsonObject();

		if (object.has(KEY)) {
			info.setReturnValue(new JmxlModelElement(from, to, faces, rotation, shade, GSON.fromJson(object.get(KEY), BlendMode.class)));
		}
	}

}
