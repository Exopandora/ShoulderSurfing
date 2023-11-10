package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Redirect
	(
		method = "handleKeybinds",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/GameSettings.setCameraType(Lnet/minecraft/client/settings/PointOfView;)V"
		)
	)
	private void setCameraType(GameSettings options, PointOfView cameraType)
	{
		ShoulderInstance.getInstance().changePerspective(Perspective.current().next());
	}
	
	@Inject
	(
		method = "handleKeybinds",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/settings/PointOfView.isFirstPerson()Z",
			shift = Shift.BEFORE,
			ordinal = 0
		)
	)
	private void preIsFirstPerson(CallbackInfo ci)
	{
		if(Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(Perspective.current());
		}
	}
}
