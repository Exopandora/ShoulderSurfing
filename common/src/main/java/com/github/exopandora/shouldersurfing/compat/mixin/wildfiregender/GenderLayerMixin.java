package com.github.exopandora.shouldersurfing.compat.mixin.wildfiregender;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "com.wildfire.render.GenderLayer")
public class GenderLayerMixin {
	@ModifyVariable(
		method = "renderBox",
		at = @At("HEAD"),
		index = 8,
		argsOnly = true,
		remap = false
	)
	private static float color(float color) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
