package com.teamderpy.shouldersurfing.config;

import javax.annotation.Nullable;

import net.minecraft.world.phys.HitResult;

public enum CrosshairVisibility
{
	ALWAYS,
	NEVER,
	WHEN_AIMING,
	WHEN_IN_RANGE,
	WHEN_AIMING_OR_IN_RANGE;
	
	public boolean doRender(@Nullable HitResult hitResult, boolean isAiming)
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
			return hitResult != null && !HitResult.Type.MISS.equals(hitResult.getType());
		}
		else if(this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE)
		{
			return CrosshairVisibility.WHEN_IN_RANGE.doRender(hitResult, isAiming) || CrosshairVisibility.WHEN_AIMING.doRender(hitResult, isAiming);
		}
		
		return true;
	}
}