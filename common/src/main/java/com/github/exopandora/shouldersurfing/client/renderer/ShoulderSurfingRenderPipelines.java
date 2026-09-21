package com.github.exopandora.shouldersurfing.client.renderer;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.oit.OitPipelineSet;

public class ShoulderSurfingRenderPipelines {
	public static RenderPipeline ARMOR_TRANSLUCENT_NO_CULL;
	public static RenderPipeline ARMOR_TRANSLUCENT_NO_CULL_GLINT;
	public static RenderPipeline ARMOR_TRIM_TRANSLUCENT;
	public static RenderPipeline ARMOR_DECAL_TRANSLUCENT_NO_CULL;
	public static RenderPipeline GLINT_TRANSLUCENT;
	
	public static OitPipelineSet OIT_ENTITY_TRANSLUCENT_GLINT;
	public static OitPipelineSet OIT_TRIMMED_ARMOR_GLINT_TRANSLUCENT;
	public static OitPipelineSet OIT_ENTITY_TRANSLUCENT;
}
