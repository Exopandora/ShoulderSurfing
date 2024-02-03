package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

@Mixin(RenderType.class)
public abstract class MixinRenderType extends RenderStateShard
{
	public MixinRenderType(String $$0, Runnable $$1, Runnable $$2)
	{
		super($$0, $$1, $$2);
	}
	
	@ModifyArg
	(
		method = "createArmorCutoutNoCull",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder.setTransparencyState(Lnet/minecraft/client/renderer/RenderStateShard$TransparencyStateShard;)Lnet/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder;"
		)
	)
	private static RenderStateShard.TransparencyStateShard setTransparencyState(RenderStateShard.TransparencyStateShard p_110686_)
	{
		return TRANSLUCENT_TRANSPARENCY;
	}
}
