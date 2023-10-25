package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;

@Mixin(Options.class)
public abstract class MixinOptions
{
	@Shadow
	private CameraType cameraType;
	
	@Inject
	(
		at = @At("HEAD"),
		method = "setCameraType"
	)
	public void setCameraType(CameraType cameraType, CallbackInfo ci)
	{
		if(cameraType != this.cameraType)
		{
			ShoulderInstance.getInstance().setShoulderSurfing(Config.CLIENT.replaceDefaultPerspective() && cameraType.equals(CameraType.THIRD_PERSON_BACK));
		}
	}
}
