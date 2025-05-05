package com.github.exopandora.shouldersurfing.compat.mixins.skinlayers;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "dev.tr7zw.skinlayers.render.CustomizableModelPart")
public class MixinCustomizableModelPart_1_6_6
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "compile",
		index = 6,
		argsOnly = true,
		remap = false
	)
	private int render(int color)
	{
		return ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
