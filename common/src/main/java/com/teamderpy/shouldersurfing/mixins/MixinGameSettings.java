package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.PointOfView;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings
{
	@Shadow
	private PointOfView cameraType;
	
	@Overwrite
	public void setCameraType(PointOfView cameraType)
	{
		if(cameraType != this.cameraType)
		{
			ShoulderInstance.getInstance().setShoulderSurfing(Config.CLIENT.replaceDefaultPerspective() && cameraType.equals(PointOfView.THIRD_PERSON_BACK));
		}
		
		this.cameraType = cameraType;
	}
}
