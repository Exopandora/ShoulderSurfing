package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.mixinducks.GuiDuck;
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
public abstract class MixinGui implements GuiDuck
{
	@Shadow
	private @Final Minecraft minecraft;
	
	/**
	 * Targets `new LayeredDraw().add(this::renderCrosshair)`
	 */
	@ModifyArg
	(
		method="<init>",
		at = @At
		(
			value = "INVOKE",
			ordinal = 1,
			target = "net/minecraft/client/gui/LayeredDraw.add(Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/minecraft/client/gui/LayeredDraw;"
		),
		index = 0
	)
	private Layer wrapRenderCrosshair(Layer original)
	{
		return this::shouldersurfing$renderCrosshair;
	}
}
