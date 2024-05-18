package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Perspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	private @Final Minecraft minecraft;
	
	@Inject
	(
		method = "renderCrosshair",
		at = @At("HEAD")
	)
	private void offsetCrosshair(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), partialTick);
	}
	
	@Inject
	(
		method = "renderCrosshair",
		at = @At("RETURN")
	)
	private void clearCrosshairOffset(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().clearCrosshairOffset(guiGraphics.pose());
	}
}
