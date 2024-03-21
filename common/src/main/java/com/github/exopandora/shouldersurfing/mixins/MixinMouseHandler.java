package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Perspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public class MixinMouseHandler
{
	@Redirect
	(
		method = "turnPlayer",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING.equals(Perspective.current());
	}
	
	@Redirect
	(
		method = "turnPlayer",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/player/LocalPlayer.turn(DD)V"
		)
	)
	private void turn(LocalPlayer player, double yRot, double xRot)
	{
		if(!ShoulderRenderer.getInstance().turn(player, yRot, xRot))
		{
			player.turn(yRot, xRot);
		}
	}
}
