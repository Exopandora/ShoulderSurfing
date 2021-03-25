package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum CrosshairVisibility
{
	ALWAYS,
	NEVER,
	WHEN_AIMING,
	WHEN_IN_RANGE,
	WHEN_AIMING_OR_IN_RANGE;
	
	public boolean doRender()
	{
		if(this == CrosshairVisibility.NEVER)
		{
			return false;
		}
		else if(this == CrosshairVisibility.WHEN_AIMING)
		{
			return ClientEventHandler.isAiming;
		}
		else if(this == CrosshairVisibility.WHEN_IN_RANGE)
		{
			return Minecraft.getInstance().hitResult != null && !Minecraft.getInstance().hitResult.getType().equals(RayTraceResult.Type.MISS);
		}
		else if(this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE)
		{
			return CrosshairVisibility.WHEN_IN_RANGE.doRender() || CrosshairVisibility.WHEN_AIMING.doRender();
		}
		
		return true;
	}
}