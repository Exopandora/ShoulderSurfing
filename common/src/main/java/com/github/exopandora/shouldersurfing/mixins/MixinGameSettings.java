package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinducks.GameSettingsDuck;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings implements GameSettingsDuck
{
	@Shadow
	private PointOfView cameraType;
	
	@Inject
	(
		at = @At("HEAD"),
		method = "setCameraType"
	)
	public void setCameraType(PointOfView cameraType, CallbackInfo ci)
	{
		if(cameraType != this.cameraType)
		{
			ShoulderSurfingImpl.getInstance().setShoulderSurfing(Config.CLIENT.replaceDefaultPerspective() && cameraType.equals(PointOfView.THIRD_PERSON_BACK));
		}
	}
	
	@Unique
	public void shouldersurfing$setCameraTypeDirect(PointOfView cameraType)
	{
		this.cameraType = cameraType;
	}
}
