package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.renderer.DebugScreenOverlayHandler;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(
		method = "getGameInformation",
		at = @At("RETURN")
	)
	private void getGameInformation(CallbackInfoReturnable<List<String>> cir) {
		DebugScreenOverlayHandler.appendDebugText(cir.getReturnValue());
	}
}
