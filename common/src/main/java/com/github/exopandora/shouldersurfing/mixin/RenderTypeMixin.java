package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin extends RenderStateShard {
	public RenderTypeMixin(String name, Runnable setupState, Runnable clearState) {
		super(name, setupState, clearState);
	}
	
	@ModifyArg(
		method = "createArmorCutoutNoCull",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder.setTransparencyState(Lnet/minecraft/client/renderer/RenderStateShard$TransparencyStateShard;)Lnet/minecraft/client/renderer/RenderType$CompositeState$CompositeStateBuilder;"
		)
	)
	private static RenderStateShard.TransparencyStateShard setTransparencyState(RenderStateShard.TransparencyStateShard transparencyStateShard) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			return TRANSLUCENT_TRANSPARENCY;
		}
		return transparencyStateShard;
	}
}
