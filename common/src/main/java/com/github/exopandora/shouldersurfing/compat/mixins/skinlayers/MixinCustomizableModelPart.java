package com.github.exopandora.shouldersurfing.compat.mixins.skinlayers;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "dev.tr7zw.skinlayers.render.CustomizableModelPart")
public class MixinCustomizableModelPart
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "compile",
		index = 8,
		argsOnly = true,
		remap = false
	)
	private float compile(float alpha)
	{
		return Math.min(alpha, ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().getCameraEntityAlpha());
	}
}