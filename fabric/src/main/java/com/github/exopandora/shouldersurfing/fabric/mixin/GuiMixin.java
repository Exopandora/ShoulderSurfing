package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow
	private @Final Minecraft minecraft;
	
	@Inject(
		method = "renderCrosshair",
		at = @At("HEAD"),
		cancellable = true
	)
	private void preRenderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
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
	private void postRenderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
		ShoulderSurfing.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics);
	}
	
	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType) {
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current() && this.minecraft.player.isScoping();
	}
}
