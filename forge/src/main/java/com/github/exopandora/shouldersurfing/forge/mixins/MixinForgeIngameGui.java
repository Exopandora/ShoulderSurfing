package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import net.minecraft.client.CameraType;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeIngameGui.class)
public class MixinForgeIngameGui
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
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}