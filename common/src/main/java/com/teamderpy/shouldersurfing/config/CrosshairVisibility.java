package com.teamderpy.shouldersurfing.config;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.RayTraceResult;

public enum CrosshairVisibility
{
	ALWAYS,
	NEVER,
	WHEN_AIMING,
	WHEN_IN_RANGE,
	WHEN_AIMING_OR_IN_RANGE;
	
	public boolean doRender(@Nullable RayTraceResult hitResult, boolean isAiming)
	{
		if(this == CrosshairVisibility.NEVER)
		{
			return false;
		}
		else if(this == CrosshairVisibility.WHEN_AIMING)
		{
			return isAiming;
		}
		else if(this == CrosshairVisibility.WHEN_IN_RANGE)
		{
			return hitResult != null && !RayTraceResult.Type.MISS.equals(hitResult.getType());
		}
		else if(this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE)
		{
			return CrosshairVisibility.WHEN_IN_RANGE.doRender(hitResult, isAiming) || CrosshairVisibility.WHEN_AIMING.doRender(hitResult, isAiming);
		}
		
		return true;
	}
}