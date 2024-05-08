package com.github.exopandora.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	protected Minecraft minecraft;
	
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
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(Minecraft.getInstance().hitResult, ShoulderInstance.getInstance().isAiming());
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
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING.equals(Perspective.current()) && this.minecraft.player.isScoping();
	}
}
