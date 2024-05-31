package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinducks.OptionsDuck;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class MixinOptions implements OptionsDuck
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
			ShoulderSurfingImpl.getInstance().setShoulderSurfing(Config.CLIENT.replaceDefaultPerspective() && cameraType.equals(CameraType.THIRD_PERSON_BACK));
		}
	}
	
	@Unique
	public void shouldersurfing$setCameraTypeDirect(CameraType cameraType)
	{
		this.cameraType = cameraType;
	}
}
