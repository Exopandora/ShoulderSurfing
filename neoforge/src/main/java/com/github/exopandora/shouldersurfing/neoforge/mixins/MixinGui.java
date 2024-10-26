package com.github.exopandora.shouldersurfing.neoforge.mixins;

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
	 * Targets `new GuiLayerManager().add(CROSSHAIR, this::renderCrosshair)`
	 * The target for Neoforge, is different from for Forge and Fabric
	 */
	@ModifyArg
	(
		method="<init>",
		at = @At
		(
			value = "INVOKE",
			ordinal = 4,
			target = "net/neoforged/neoforge/client/gui/GuiLayerManager.add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/neoforged/neoforge/client/gui/GuiLayerManager;"
		),
		index = 1
	)
	private Layer wrapRenderCrosshair(Layer original)
	{
		return this::shouldersurfing$renderCrosshair;
	}
}
