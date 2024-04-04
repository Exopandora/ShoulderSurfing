package com.github.exopandora.shouldersurfing.config;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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
		else if(this == CrosshairType.DYNAMIC || this == CrosshairType.DYNAMIC_WITH_1PP)
		{
			return !(entity instanceof Player player && player.isScoping());
		}
		
		return false;
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