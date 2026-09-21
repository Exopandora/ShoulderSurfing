package com.github.exopandora.shouldersurfing.compat.mixin.iris;

import com.github.exopandora.shouldersurfing.client.renderer.ShoulderSurfingRenderPipelines;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.exopandora.shouldersurfing.client.renderer.ShoulderSurfingRenderPipelines.GLINT_TRANSLUCENT;

@Mixin(IrisPipelines.class)
class IrisPipelinesMixin {
	@Inject(
		method = "<clinit>",
		at = @At("TAIL")
	)
	private static void clinit(CallbackInfo ci) {
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL_GLINT, ShaderKey.ENTITIES_TRANSLUCENT_GLINT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_TRIM_TRANSLUCENT, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.ARMOR_DECAL_TRANSLUCENT_NO_CULL, ShaderKey.ENTITIES_TRANSLUCENT);
		IrisPipelines.assignPipeline(ShoulderSurfingRenderPipelines.GLINT_TRANSLUCENT, ShaderKey.ENTITIES_TRANSLUCENT_GLINT);
	}
}
