package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ShoulderSurfingRenderTypes
{
	public static RenderType ARMOR_ENTITY_GLINT_ITEM_TARGET;
	public static Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT_ITEM_TARGET;
	public static Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT;
	
	public static RenderType armorEntityGlintItemTarget()
	{
		return ARMOR_ENTITY_GLINT_ITEM_TARGET;
	}
	
	public static RenderType armorTranslucentItemTarget(ResourceLocation texture)
	{
		return ARMOR_TRANSLUCENT_ITEM_TARGET.apply(texture);
	}
	
	public static RenderType armorTranslucent(ResourceLocation texture)
	{
		return ARMOR_TRANSLUCENT.apply(texture);
	}
}
