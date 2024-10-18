package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	private @Final Minecraft minecraft;
	@Shadow
	private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker){ throw new AssertionError(); }

	/**
	 * Targets `new LayeredDraw().add(this::renderCrosshair)`
	 */
	@ModifyArg(
		method="<init>",
		at = @At(value="INVOKE", target="net/minecraft/client/gui/LayeredDraw.add(Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 1),
		index = 0
	)
	private Layer WrapRenderCrosshair(Layer original){
		return this::renderCrosshairWrapper;
	}

	@Unique
	private void renderCrosshairWrapper(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
		CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();

		// Draw primary crosshair
		crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow());

		if(!crosshairRenderer.doRenderCrosshair())
		{
			return;
		}
		this.renderCrosshair(guiGraphics, deltaTracker);
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics.pose());


		// Draw secondary crosshair
		// if (crosshairRenderer.isCrosshairDynamic(minecraft.getCameraEntity())){

		// }
	}

}
