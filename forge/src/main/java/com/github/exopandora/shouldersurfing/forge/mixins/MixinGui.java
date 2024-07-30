package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	private @Final Minecraft minecraft;
	
	@Inject
	(
		method = "renderCrosshair",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	private void offsetCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().getCrosshairRenderer().offsetCrosshair(guiGraphics.pose(), this.minecraft.getWindow()))
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "renderCrosshair",
		at = @At("RETURN"),
		remap = false
	)
	private void clearCrosshairOffset(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().clearCrosshairOffset(guiGraphics.pose());
	}
}