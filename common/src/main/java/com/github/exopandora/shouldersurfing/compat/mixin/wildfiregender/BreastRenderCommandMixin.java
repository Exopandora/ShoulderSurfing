package com.github.exopandora.shouldersurfing.compat.mixin.wildfiregender;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "com.wildfire.render.BreastRenderCommand")
public class BreastRenderCommandMixin {
	@ModifyVariable(
		method = "<init>",
		at = @At("HEAD"),
		index = 4,
		argsOnly = true,
		remap = false
	)
	private static int color(int color) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
