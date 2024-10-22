package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.mixinducks.GuiDuck;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class MixinGui implements GuiDuck
{
	@Shadow
	private @Final Minecraft minecraft;
	@Shadow
	private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker){ throw new AssertionError(); }
	
	@Redirect
	(
		method = "renderCrosshair",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType)
	{
		return ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair();
	}
	
	@Redirect
	(
		method = "renderCameraOverlays",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current() && this.minecraft.player.isScoping();
	}
	
	
	/**
	 * Used in loader-specific mixins
	 */
	public void shouldersurfing$renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker)
	{
		CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
		if(!crosshairRenderer.doRenderCrosshair())
		{
			return;
		}
		
		// Draw primary crosshair
		crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow());
		this.renderCrosshair(guiGraphics, deltaTracker);
		crosshairRenderer.postRenderCrosshair(guiGraphics.pose());
		
		// Draw obstruction crosshair
		if (crosshairRenderer.doRenderObstructionCrosshair())
		{
			crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), true);
			this.renderCrosshair(guiGraphics, deltaTracker);
			crosshairRenderer.postRenderCrosshair(guiGraphics.pose(), true);
		}
	}
}
