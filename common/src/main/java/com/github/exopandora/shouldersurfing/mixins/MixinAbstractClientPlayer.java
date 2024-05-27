package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.CameraType;
import net.minecraft.client.player.AbstractClientPlayer;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer
{
	@Redirect
	(
		method = "getFieldOfViewModifier",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}
