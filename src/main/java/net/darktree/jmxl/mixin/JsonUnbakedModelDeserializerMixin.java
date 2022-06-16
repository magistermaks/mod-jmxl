package net.darktree.jmxl.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.darktree.jmxl.client.JmxlModelElement;
import net.darktree.jmxl.client.JmxlUnbakedModel;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Mixin(JsonUnbakedModel.Deserializer.class)
public abstract class JsonUnbakedModelDeserializerMixin {

	private final static String KEY = "jmxl";

	@Inject(method="deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", at=@At("TAIL"), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
	public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<JsonUnbakedModel> info, JsonObject jsonObject, List<ModelElement> elements, String string, Map<String, Either<SpriteIdentifier, String>> sprites, boolean ao, ModelTransformation transformation, List<ModelOverride> overrides, JsonUnbakedModel.GuiLight light, Identifier identifier) throws JsonParseException {
		JsonObject object = jsonElement.getAsJsonObject();

		if (object.has(KEY) && object.get(KEY).getAsBoolean()) {
			info.setReturnValue(new JmxlUnbakedModel(identifier, elements, sprites, ao, light, transformation, overrides));
		}
	}

}
