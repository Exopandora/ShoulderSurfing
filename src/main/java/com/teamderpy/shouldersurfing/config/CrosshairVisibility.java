package com.teamderpy.shouldersurfing.config;

import javax.annotation.Nullable;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum CrosshairVisibility
{
	ALWAYS,
	NEVER,
	WHEN_AIMING,
	WHEN_IN_RANGE,
	WHEN_AIMING_OR_IN_RANGE;
	
	public boolean doRender(@Nullable MovingObjectPosition hitResult, boolean isAiming)
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
			return hitResult != null && !MovingObjectPosition.MovingObjectType.MISS.equals(hitResult.typeOfHit);
		}
		else if(this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE)
		{
			return CrosshairVisibility.WHEN_IN_RANGE.doRender(hitResult, isAiming) || CrosshairVisibility.WHEN_AIMING.doRender(hitResult, isAiming);
		}
		
		return true;
	}
}