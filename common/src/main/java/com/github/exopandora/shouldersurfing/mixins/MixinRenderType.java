package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public class MixinRenderType
{
	@Inject
	(
		at = @At("HEAD"),
		method = "armorCutoutNoCull",
		cancellable = true
	)
	private static void armorCutoutNoCull(ResourceLocation resourceLocation, CallbackInfoReturnable<RenderType> cir)
	{
		if(Config.CLIENT.isPlayerTransparencyEnabled())
		{
			cir.setReturnValue(RenderType.armorTranslucent(resourceLocation));
		}
	}
}
