package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.PointOfView;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings
{
	@Shadow
	private PointOfView cameraType;
	
	/**
	 * @author Exopandora
	 * @reason Disable shoulder surfing perspective when other mods change the perspective
	 */
	@Overwrite
	public void setCameraType(PointOfView cameraType)
	{
		if(cameraType != this.cameraType)
		{
			ShoulderSurfing.shoulderSurfing = false;
		}
		
		this.cameraType = cameraType;
	}
}
