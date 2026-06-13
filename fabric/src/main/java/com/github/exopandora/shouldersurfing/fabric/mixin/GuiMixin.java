package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(
		method = "extractCrosshair",
		at = @At("HEAD"),
		cancellable = true
	)
	private void preRenderCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		CrosshairRenderer crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
		if (crosshairRenderer.isCrosshairVisible()) {
			crosshairRenderer.preRenderCrosshair(guiGraphics);
		} else {
			ci.cancel();
		}
	}
	
	@Inject(
		method = "extractCrosshair",
		at = @At("RETURN")
	)
	private void postRenderCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		ShoulderSurfing.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics);
	}
}
