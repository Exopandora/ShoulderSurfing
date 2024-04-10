package com.github.exopandora.shouldersurfing.mixins.compatibility;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = {"dev.tr7zw.skinlayers.render.CustomizableModelPart"})
public class MixinCustomizableModelPart
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "compile",
		index = 8,
		argsOnly = true,
		remap = false,
		require = 0
	)
	private float render(float alpha)
	{
		return Math.min(alpha, ShoulderRenderer.getInstance().getCameraEntityAlpha());
	}
}