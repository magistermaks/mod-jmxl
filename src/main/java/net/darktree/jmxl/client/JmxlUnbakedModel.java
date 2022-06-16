package net.darktree.jmxl.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class JmxlUnbakedModel extends JsonUnbakedModel {

	public JmxlUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, boolean ambientOcclusion, @Nullable JsonUnbakedModel.GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
		super(parentId, elements, textureMap, ambientOcclusion, guiLight, transformations, overrides);
	}

}
