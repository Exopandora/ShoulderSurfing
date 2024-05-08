package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	protected Minecraft minecraft;
	
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
