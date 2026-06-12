package com.github.exopandora.shouldersurfing.compat.mixin.mts;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "minecrafttransportsimulator.systems.CameraSystem")
public class CameraSystemMixin {
	@Inject(
		method = "adjustCamera",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void adjustCamera(CallbackInfoReturnable<Boolean> ci) {
		if (ShoulderSurfing.getInstance().isShoulderSurfing()) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}
}
