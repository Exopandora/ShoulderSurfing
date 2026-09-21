package com.github.exopandora.shouldersurfing.client.renderer.rendertype;

import com.github.exopandora.shouldersurfing.client.renderer.ShoulderSurfingRenderPipelines;
import com.github.exopandora.shouldersurfing.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ShoulderSurfingRenderTypes {
	private static final Function<Identifier, RenderType> ARMOR_TRANSLUCENT_NO_CULL = Util.memoize(
		texture -> {
			RenderSetup state = RenderSetup.builder(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL)
				.setOitPipelines(ShoulderSurfingRenderPipelines.OIT_ENTITY_TRANSLUCENT)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup();
			return RenderTypeAccessor.invokeCreate("shouldersurfing_armor_cutout_no_cull_translucent", state);
		}
	);
	
	private static final Function<Identifier, RenderType> ARMOR_TRANSLUCENT_NO_CULL_GLINT = Util.memoize(
		texture -> {
			RenderSetup state = RenderSetup.builder(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL_GLINT)
				.setOitPipelines(ShoulderSurfingRenderPipelines.OIT_ENTITY_TRANSLUCENT_GLINT)
				.withTexture("Sampler0", texture)
				.withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
				.setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
				.useLightmap()
				.useOverlay()
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup();
			return RenderTypeAccessor.invokeCreate("shouldersurfing_armor_translucent_no_cull_glint", state);
		}
	);
	
	private static final Function<Identifier, RenderType> ARMOR_TRIM_TRANSLUCENT = Util.memoize(
		texture -> {
			RenderSetup state = RenderSetup.builder(ShoulderSurfingRenderPipelines.ARMOR_TRIM_TRANSLUCENT)
				.setOitPipelines(ShoulderSurfingRenderPipelines.OIT_ENTITY_TRANSLUCENT)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.affectsCrumbling()
				.sortOnUpload()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup();
			return RenderTypeAccessor.invokeCreate("shouldersurfing_armor_trim_translucent", state);
		}
	);
	
	private static final Function<Identifier, RenderType> ARMOR_TRIM_TRANSLUCENT_DECAL = Util.memoize(
		texture -> {
			RenderSetup state = RenderSetup.builder(ShoulderSurfingRenderPipelines.ARMOR_DECAL_TRANSLUCENT_NO_CULL)
				.setOitPipelines(RenderPipelines.OIT_ENTITY)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup();
			return RenderTypeAccessor.invokeCreate("shouldersurfing_armor_trim_translucent_decal", state);
		}
	);
	
	private static final RenderType TRIMMED_ARMOR_GLINT_TRANSLUCENT = RenderTypeAccessor.invokeCreate(
		"trimmed_armor_glint_translucent",
		RenderSetup.builder(ShoulderSurfingRenderPipelines.GLINT_TRANSLUCENT)
			.setOitPipelines(ShoulderSurfingRenderPipelines.OIT_TRIMMED_ARMOR_GLINT_TRANSLUCENT)
			.withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
			.setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);
	
	public static RenderType armorTranslucentNoCull(Identifier texture) {
		return ARMOR_TRANSLUCENT_NO_CULL.apply(texture);
	}
	
	public static RenderType armorTranslucentNoCullGlint(Identifier texture) {
		return ARMOR_TRANSLUCENT_NO_CULL_GLINT.apply(texture);
	}
	
	public static RenderType armorTrimTranslucent(Identifier texture, boolean decal) {
		return (decal ? ARMOR_TRIM_TRANSLUCENT_DECAL : ARMOR_TRIM_TRANSLUCENT).apply(texture);
	}
	
	public static RenderType trimmedArmorGlint() {
		return TRIMMED_ARMOR_GLINT_TRANSLUCENT;
	}
}
