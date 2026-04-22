package com.github.exopandora.shouldersurfing.mixins;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface RenderTypeAccessor
{
	@Invoker
	static RenderType invokeCreate(String name, RenderSetup state)
	{
		throw new AssertionError("Not implemented");
	}
}
