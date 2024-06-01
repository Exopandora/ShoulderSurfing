package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public enum CrosshairType
{
	ADAPTIVE,
	DYNAMIC,
	STATIC,
	STATIC_WITH_1PP,
	DYNAMIC_WITH_1PP;
	
	public boolean isDynamic(Entity entity, boolean isAiming)
	{
		if(this == CrosshairType.ADAPTIVE)
		{
			return isAiming;
		}
		
		return this == CrosshairType.DYNAMIC || this == CrosshairType.DYNAMIC_WITH_1PP;
	}
	
	public boolean isAimingDecoupled()
	{
		return this == CrosshairType.STATIC || this == CrosshairType.STATIC_WITH_1PP;
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
