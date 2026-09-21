package com.github.exopandora.shouldersurfing.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {
	@Accessor("GLOBALS_SNIPPET")
	static RenderPipeline.Snippet getGlobalsSnippet() {
		throw new AssertionError();
	}
	
	@Accessor("ENTITY_SNIPPET")
	static RenderPipeline.Snippet getEntitySnippet() {
		throw new AssertionError();
	}
	
	@Accessor("OIT_ENTITY_SNIPPET")
	static RenderPipeline.Snippet getOitEntitySnippet() {
		throw new AssertionError();
	}
	
	@Invoker
	static RenderPipeline invokeRegister(RenderPipeline pipeline) {
		throw new AssertionError();
	}
	
	@Invoker
	static OitPipelineSet invokeRegister(OitPipelineSet oitPipelineSet) {
		throw new AssertionError();
	}
}
