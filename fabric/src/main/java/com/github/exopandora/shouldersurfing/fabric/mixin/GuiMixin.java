package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.gui.MapOverlayRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
class GuiMixin {
	@Inject(
		method = "renderDemoOverlay",
		at = @At("HEAD")
	)
	private void renderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		MapOverlayRenderer.extractMapOverlay(guiGraphics, deltaTracker);
	}
	
	@Inject(
		method = "renderCrosshair",
		at = @At("HEAD"),
		cancellable = true
	)
	private void preRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
		if (crosshairRenderer.isCrosshairVisible()) {
			crosshairRenderer.preRenderCrosshair(guiGraphics);
		} else {
			ci.cancel();
		}
	}
	
	@Inject(
		method = "renderCrosshair",
		at = @At("RETURN")
	)
	private void postRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		ShoulderSurfing.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics);
	}
}
