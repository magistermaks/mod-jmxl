package net.darktree.jmxl.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.client.JmxlUnbakedModel;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.List;

@Mixin(JsonUnbakedModel.Deserializer.class)
public abstract class JsonUnbakedModelDeserializerMixin {

	@Unique
	private final static String KEY = "jmxl";

	@Inject(
			method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;",
			at = @At("TAIL"),
			cancellable = true
	)
	public void deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context, CallbackInfoReturnable<JsonUnbakedModel> info, @Local(ordinal = 0) List<ModelElement> elements, @Local ModelTextures.Textures textures, @Local Boolean ao, @Local ModelTransformation transformation, @Local JsonUnbakedModel.GuiLight light, @Local Identifier identifier) throws JsonParseException {
		JsonObject object = jsonElement.getAsJsonObject();

		if (object.has(KEY) && object.get(KEY).getAsBoolean()) {
			info.setReturnValue(new JmxlUnbakedModel(identifier, elements, textures, ao, light, transformation));
		}
	}

}
