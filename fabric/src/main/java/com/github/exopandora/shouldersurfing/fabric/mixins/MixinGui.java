package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.GuiDuck;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw.Layer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public abstract class MixinGui implements GuiDuck
{
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
