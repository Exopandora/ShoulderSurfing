package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.client.ShoulderHelper;

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
			return true;
		}
		
		return false;
	}
	
	public boolean isAimingDecoupled()
	{
		return this == CrosshairType.STATIC;
	}
	
	public boolean doSwitchPerspective()
	{
		if(this == CrosshairType.STATIC_WITH_1PP || this == CrosshairType.DYNAMIC_WITH_1PP)
		{
			return ShoulderHelper.isHoldingAdaptiveItem();
		}
		
		return false;
	}
}