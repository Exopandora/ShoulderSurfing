package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderType.class)
public abstract class MixinRenderType extends RenderStateShard
{
	public MixinRenderType(String name, Runnable setupState, Runnable clearState)
	{
		super(name, setupState, clearState);
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
	private static RenderStateShard.TransparencyStateShard setTransparencyState(RenderStateShard.TransparencyStateShard transparencyStateShard)
	{
		return Config.CLIENT.isPlayerTransparencyEnabled() ? TRANSLUCENT_TRANSPARENCY : transparencyStateShard;
	}
}