package com.github.exopandora.shouldersurfing.forge.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(
		method = "renderCrosshair",
		at = @At("HEAD"),
		cancellable = true
	)
	private void preRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		CrosshairRenderer crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
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
