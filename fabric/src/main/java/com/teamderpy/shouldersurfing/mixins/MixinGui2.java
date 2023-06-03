package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class MixinGui2
{
	@Shadow
	protected Minecraft minecraft;
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V",
			shift = Shift.BEFORE
		)
	)
	private void offsetCrosshair(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), partialTick);
	}
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V",
			shift = Shift.AFTER
		)
	)
	private void clearCrosshairOffset(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().clearCrosshairOffset(guiGraphics.pose());
	}
}
