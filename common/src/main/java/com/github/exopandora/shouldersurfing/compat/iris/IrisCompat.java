package com.github.exopandora.shouldersurfing.compat.iris;

import com.github.exopandora.shouldersurfing.client.renderer.ShoulderSurfingRenderPipelines;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.irisshaders.iris.pipeline.programs.ShaderKey;

public class IrisCompat {
	public static void assignPipelines() {
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipelineShadow(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL, ShaderKey.SHADOW_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL_GLINT, ShaderKey.ENTITIES_TRANSLUCENT_GLINT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRIM_TRANSLUCENT, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipelineShadow(ShoulderSurfingRenderPipelines.ARMOR_TRIM_TRANSLUCENT, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_DECAL_TRANSLUCENT_NO_CULL, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.GLINT_TRANSLUCENT, ShaderKey.SHADOW_TRANSLUCENT);
	}
}
