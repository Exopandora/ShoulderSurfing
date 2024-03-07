package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;

@Mixin(RenderType.class)
public abstract class MixinRenderType extends RenderState
{
	public MixinRenderType(String name, Runnable setupState, Runnable clearState)
	{
		super(name, setupState, clearState);
	}
	
	@ModifyArg
	(
		method =
		{
			"armorCutoutNoCull", // fabric/forge
			"lambda$getArmorCutoutNoCull$0" // optifine
		},
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType$State$Builder.setTransparencyState(Lnet/minecraft/client/renderer/RenderState$TransparencyState;)Lnet/minecraft/client/renderer/RenderType$State$Builder;"
		),
		require = 1
	)
	private static RenderState.TransparencyState setTransparencyState(RenderState.TransparencyState transparencyState)
	{
		return TRANSLUCENT_TRANSPARENCY;
	}
}
