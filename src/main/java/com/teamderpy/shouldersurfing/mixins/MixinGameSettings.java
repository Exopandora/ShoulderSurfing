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
	private PointOfView pointOfView;
	
	@Overwrite
	public void setPointOfView(PointOfView pointOfView)
	{
		if(pointOfView != this.pointOfView)
		{
			ShoulderSurfing.shoulderSurfing = false;
		}
		
		this.pointOfView = pointOfView;
	}
}
