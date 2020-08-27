package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.PointOfView;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings
{
	@Shadow
	private PointOfView field_243228_bb;
	
	@Overwrite
	public void func_243229_a(PointOfView pointOfView)
	{
		if(pointOfView != this.field_243228_bb)
		{
			ClientEventHandler.shoulderSurfing = false;
		}
		
		this.field_243228_bb = pointOfView;
	}
}
