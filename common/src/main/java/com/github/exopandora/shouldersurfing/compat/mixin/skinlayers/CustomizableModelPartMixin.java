package com.github.exopandora.shouldersurfing.compat.mixin.skinlayers;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "dev.tr7zw.skinlayers.render.CustomizableModelPart")
public class CustomizableModelPartMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "compile",
		index = 6,
		argsOnly = true,
		remap = false
	)
	private int compile(int color) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
