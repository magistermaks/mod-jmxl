package net.darktree.jmxl.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.darktree.jmxl.client.JmxlInitializer;
import net.darktree.jmxl.duck.JmxlElement;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(targets={"net.minecraft.client.renderer.block.model.BlockElement$Deserializer"})
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
			method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElement;",
			at = @At(
					value = "NEW",
					args = "class=net/minecraft/client/renderer/block/model/BlockElement"
			)
	)
	public BlockElement deserialize(Vector3fc from, Vector3fc to, Map<Direction, BlockElementFace> faces, @Nullable BlockElementRotation rotation, boolean shade, int light, Operation<BlockElement> original, @Local(ordinal = 0) JsonObject json) throws JsonParseException {
		BlockElement element = original.call(from, to, faces, rotation, shade, light);

		if (json.has(EMISSIVE)) {
			JmxlInitializer.LOGGER.error("Emissivity is a vanilla features now, replace boolean 'jmxl_emissive' with integer 'light_emission'!");
		}

		// technically this check is optional
		if (json.has(LAYER) || json.has(DIFFUSE) || json.has(AMBIENT)) {

			ChunkSectionLayer blend = json.has(LAYER) ? GSON.fromJson(json.get(LAYER), ChunkSectionLayer.class) : ChunkSectionLayer.SOLID;
			boolean diffuse = getBoolean(json, DIFFUSE, false);

			JmxlElement jmxl = ((JmxlElement) (Object) element);
			jmxl.jmxl_setRenderLayer(blend);
			jmxl.jmxl_setDiffuse(diffuse);

			if (json.has(AMBIENT)) {
				jmxl.jmxl_setAmbientOcclusion(TriState.of(json.get(AMBIENT).getAsBoolean()));
			}

		}

		return element;
	}

	@Unique
	private boolean getBoolean(JsonObject object, String key, boolean def) {
		return object.has(key) ? object.get(key).getAsBoolean() : def;
	}

}
