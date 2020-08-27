package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum CrosshairType
{
	ADAPTIVE,
	DYNAMIC,
	STATIC,
	STATIC_WITH_1PP;
	
	public boolean isDynamic()
	{
		if(this == CrosshairType.ADAPTIVE)
		{
			return ClientEventHandler.isHoldingSpecialItem();
		}
		else if(this == CrosshairType.DYNAMIC)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean doSwitchPerspective()
	{
		if(this == CrosshairType.STATIC_WITH_1PP)
		{
			return ClientEventHandler.isHoldingSpecialItem();
		}
		
		return false;
	}
}