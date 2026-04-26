package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.mixins.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ShoulderSurfingRenderTypes
{
	private static final RenderType ARMOR_ENTITY_GLINT_ITEM_TARGET = RenderTypeAccessor.invokeCreate(
		"armor_entity_glint_item_target",
		RenderSetup.builder(RenderPipelines.GLINT)
			.withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
			.setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup()
	);
	
	private static final Function<Identifier, RenderType> ARMOR_TRANSLUCENT_ITEM_TARGET = Util.memoize(texture ->
	{
		RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_TRANSLUCENT)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup();
		return RenderTypeAccessor.invokeCreate("armor_translucent_item_target", state);
	});
	
	private static final Function<Identifier, RenderType> ENTITY_TRANSLUCENT_ITEM_TARGET = Util.memoize(texture ->
	{
		RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
			.withTexture("Sampler0", texture)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup();
		return RenderTypeAccessor.invokeCreate("entity_translucent_item_target", state);
	});
	
	public static RenderType armorEntityGlintItemTarget()
	{
		return ARMOR_ENTITY_GLINT_ITEM_TARGET;
	}
	
	public static RenderType armorTranslucentItemTarget(Identifier texture)
	{
		return ARMOR_TRANSLUCENT_ITEM_TARGET.apply(texture);
	}
	
	public static RenderType entityTranslucentItemTarget(Identifier identifier)
	{
		return ENTITY_TRANSLUCENT_ITEM_TARGET.apply(identifier);
	}
}
