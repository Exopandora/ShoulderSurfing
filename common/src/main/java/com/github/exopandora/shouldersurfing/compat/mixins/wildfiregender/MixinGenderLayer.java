package com.github.exopandora.shouldersurfing.compat.mixins.wildfiregender;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderLayer")
public class MixinGenderLayer
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "renderBox",
		index = 8,
		argsOnly = true,
		remap = false
	)
	private static float renderBox(float alpha)
	{
		return ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(alpha);
	}
}
