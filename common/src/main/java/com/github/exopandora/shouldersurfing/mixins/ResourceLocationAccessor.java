package com.github.exopandora.shouldersurfing.mixins;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceLocation.class)
public interface ResourceLocationAccessor
{
	@Invoker
	static boolean invokeIsValidNamespace(String namespace)
	{
		throw new AssertionError();
	}
}
