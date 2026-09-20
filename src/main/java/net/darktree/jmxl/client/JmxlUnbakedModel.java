package net.darktree.jmxl.client;

import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JmxlUnbakedModel extends JsonUnbakedModel {

	public JmxlUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, ModelTextures.Textures textures, @Nullable Boolean ao, @Nullable JsonUnbakedModel.GuiLight guiLight, ModelTransformation transformations) {
		super(parentId, elements, textures, ao, guiLight, transformations);
	}

}
