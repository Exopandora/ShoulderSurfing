package com.github.exopandora.shouldersurfing.neoforge.mixins;

import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.exopandora.shouldersurfing.config.Perspective;

import net.minecraft.client.CameraType;

@Mixin(ExtendedGui.class)
public class MixinExtendedGui
{
	@Redirect
	(
		method = "renderSpyglassOverlay",
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
}
