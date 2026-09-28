package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.renderer.ShoulderSurfingRenderPipelines;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.iris.IrisCompat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.minecraft.client.renderer.RenderPipelines.GLINT_SNIPPET;

@Mixin(RenderPipelines.class)
class RenderPipelinesMixin {
	@Inject(
		method = "<clinit>",
		at = @At("TAIL")
	)
	private static void init(CallbackInfo ci) {
		final var trimmedArmorGlintTranslucentSnippet = RenderPipeline.builder(RenderPipelinesAccessor.getGlobalsSnippet())
			.withBindGroupLayout(BindGroupLayouts.PROJECTION)
			.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withVertexShader("core/glint")
			.withFragmentShader("core/glint")
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withCull(false)
			.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
			.buildSnippet();
		
		ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL = RenderPipelinesAccessor.invokeRegister(
			RenderPipeline.builder(RenderPipelinesAccessor.getEntitySnippet())
				.withLocation(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pipeline/armor_translucent_no_cull"))
				.withShaderDefine("ALPHA_CUTOUT", 0.1F)
				.withShaderDefine("NO_OVERLAY")
				.withShaderDefine("PER_FACE_LIGHTING")
				.withCull(false)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.build()
		);
		ShoulderSurfingRenderPipelines.ARMOR_TRANSLUCENT_NO_CULL_GLINT = RenderPipelinesAccessor.invokeRegister(
			RenderPipeline.builder(RenderPipelinesAccessor.getEntitySnippet(), GLINT_SNIPPET)
				.withFragmentShader(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "core/entity_translucent"))
				.withLocation(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pipeline/armor_translucent_no_cull_glint"))
				.withShaderDefine("ALPHA_CUTOUT", 0.1F)
				.withShaderDefine("NO_OVERLAY")
				.withShaderDefine("PER_FACE_LIGHTING")
				.withCull(false)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.build()
		);
		ShoulderSurfingRenderPipelines.ARMOR_TRIM_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
			RenderPipeline.builder(RenderPipelinesAccessor.getEntitySnippet())
				.withLocation(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pipeline/armor_trim_translucent"))
				.withShaderDefine("ALPHA_CUTOUT", 0.1F)
				.withShaderDefine("NO_OVERLAY")
				.withShaderDefine("PER_FACE_LIGHTING")
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.withCull(false)
				.build()
		);
		ShoulderSurfingRenderPipelines.ARMOR_DECAL_TRANSLUCENT_NO_CULL = RenderPipelinesAccessor.invokeRegister(
			RenderPipeline.builder(RenderPipelinesAccessor.getEntitySnippet())
				.withLocation(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pipeline/armor_decal_translucent_no_cull"))
				.withShaderDefine("ALPHA_CUTOUT", 0.1F)
				.withShaderDefine("NO_OVERLAY")
				.withShaderDefine("PER_FACE_LIGHTING")
				.withCull(false)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
				.build()
		);
		ShoulderSurfingRenderPipelines.GLINT_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
			RenderPipeline.builder(trimmedArmorGlintTranslucentSnippet)
				.withLocation(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pipeline/glint_translucent"))
				.withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
				.build()
		);
		
		ShoulderSurfingRenderPipelines.OIT_ENTITY_TRANSLUCENT_GLINT = RenderPipelinesAccessor.invokeRegister(
			OitPipelineSet.builder(
					"shouldersurfing_entity_translucent_glint",
					RenderPipeline.builder(RenderPipelinesAccessor.getOitEntitySnippet()).withCull(false)
				)
				.withAccumulateModifier(
					builder -> builder.withSnippet(GLINT_SNIPPET)
						.withBindGroupLayout(BindGroupLayouts.GLOBALS)
						.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
						.withBindGroupLayout(BindGroupLayouts.SAMPLER2)
				)
				.build()
		);
		ShoulderSurfingRenderPipelines.OIT_TRIMMED_ARMOR_GLINT_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
			OitPipelineSet.builder(
					"shouldersurfing_trimmed_armor_glint_translucent",
					RenderPipeline.builder(trimmedArmorGlintTranslucentSnippet).withCull(false)
				)
				.withTransmittanceModifier(
					builder -> builder
						.withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
				)
				.withDepthBoundsModifier(
					builder -> builder
						.withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
				)
				.withAccumulateModifier(
					builder -> builder
						.withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
						.withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.GLINT), GpuFormat.RGBA16_FLOAT, 15))
						.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
						.withBindGroupLayout(BindGroupLayouts.SAMPLER2)
				)
				.build()
		);
		ShoulderSurfingRenderPipelines.OIT_ENTITY_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
			OitPipelineSet.builder(
					"entity",
					RenderPipeline.builder(RenderPipelinesAccessor.getOitEntitySnippet()).withCull(false)
				)
				.withAccumulateModifier(
					builder -> builder
						.withShaderDefine("PER_FACE_LIGHTING")
						.withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), GpuFormat.RGBA16_FLOAT, 15))
						.withBindGroupLayout(BindGroupLayouts.SAMPLER1)
						.withBindGroupLayout(BindGroupLayouts.SAMPLER2)
				)
				.build()
		);
		
		if (Mods.IRIS.isLoaded()) {
			IrisCompat.assignPipelines();
		}
	}
}
