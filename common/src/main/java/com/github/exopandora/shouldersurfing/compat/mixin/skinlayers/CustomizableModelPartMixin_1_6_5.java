package com.github.exopandora.shouldersurfing.compat.mixin.skinlayers;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "dev.tr7zw.skinlayers.render.CustomizableModelPart")
public class CustomizableModelPartMixin_1_6_5 {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "compile",
		index = 9,
		argsOnly = true,
		remap = false
	)
	private float compile(float alpha) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(alpha);
	}
}
