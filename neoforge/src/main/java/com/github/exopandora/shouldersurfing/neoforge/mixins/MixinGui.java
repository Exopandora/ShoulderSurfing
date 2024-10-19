package com.github.exopandora.shouldersurfing.neoforge.mixins;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
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
	 * Targets `new GuiLayerManager().add(CROSSHAIR, this::renderCrosshair)`
	 * The target for Neoforge, is different than for Forge and Fabric !!
	 */
	@ModifyArg(
		method="<init>",
		at = @At(value="INVOKE", ordinal=4, target="net/neoforged/neoforge/client/gui/GuiLayerManager.add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/neoforged/neoforge/client/gui/GuiLayerManager;"),
		index = 1
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
		crosshairRenderer.postRenderCrosshair(guiGraphics.pose());


		// Draw secondary crosshair
		if (crosshairRenderer.doRenderSecondaryCrosshair()){
			crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), true);
			this.renderCrosshair(guiGraphics, deltaTracker);
			crosshairRenderer.postRenderCrosshair(guiGraphics.pose(), true);
		}
	}

}
