package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Inject
	(
		method = "renderCrosshair",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	private void preRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
	{
		CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
		
		if(crosshairRenderer.doRenderCrosshair())
		{
			crosshairRenderer.preRenderCrosshair(guiGraphics);
		}
		else
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
	private void postRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics);
	}
}
