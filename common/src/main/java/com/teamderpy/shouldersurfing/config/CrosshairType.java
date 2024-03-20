package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public enum CrosshairType
{
	ADAPTIVE,
	DYNAMIC,
	STATIC,
	STATIC_WITH_1PP,
	DYNAMIC_WITH_1PP;
	
	public boolean isDynamic()
	{
		if(this == CrosshairType.ADAPTIVE)
		{
			return ShoulderHelper.isHoldingAdaptiveItem();
		}
		else if(this == CrosshairType.DYNAMIC || this == CrosshairType.DYNAMIC_WITH_1PP)
		{
			return !(Minecraft.getInstance().getCameraEntity() instanceof Player player && player.isScoping());
		}
		
		return false;
	}
	
	public boolean isAimingDecoupled()
	{
		return this == CrosshairType.STATIC;
	}
	
	public boolean doSwitchPerspective(boolean isAiming)
	{
		if(this == CrosshairType.STATIC_WITH_1PP || this == CrosshairType.DYNAMIC_WITH_1PP)
		{
			return isAiming;
		}
		
		return false;
	}
}