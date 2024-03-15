package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

@Mixin(RenderType.class)
public abstract class MixinRenderType extends RenderStateShard
{
	public MixinRenderType(String name, Runnable setupState, Runnable clearState)
	{
		super(name, setupState, clearState);
	}
	
	@ModifyArg
	(
		method =
		{
			"method_34827", // fabric
			"lambda$static$0(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;", // forge
			"m_173205_(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;" // optifine
		},
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder.setTransparencyState(Lnet/minecraft/client/renderer/RenderStateShard$TransparencyStateShard;)Lnet/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder;",
			remap = true
		),
		remap = false,
		require = 1
	)
	private static RenderStateShard.TransparencyStateShard setTransparencyState(RenderStateShard.TransparencyStateShard transparencyStateShard)
	{
		return TRANSLUCENT_TRANSPARENCY;
	}
}
