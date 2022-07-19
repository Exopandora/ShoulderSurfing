package com.teamderpy.shouldersurfing.client;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public class ShoulderInstance
{
	private static ShoulderInstance instance;
	private boolean doShoulderSurfing;
	private boolean doSwitchPerspective;
	private boolean isAiming;
	
	public ShoulderInstance()
	{
		instance = this;
	}
	
	public void tick(Minecraft minecraft)
	{
		if(!Perspective.FIRST_PERSON.equals(Perspective.current()))
		{
			this.doSwitchPerspective = false;
		}
		
		this.isAiming = ShoulderHelper.isHoldingSpecialItem();
		
		if(this.isAiming && Config.CLIENT.getCrosshairType().doSwitchPerspective() && this.doShoulderSurfing)
		{
			this.changePerspective(minecraft.options, Perspective.FIRST_PERSON);
			this.doSwitchPerspective = true;
		}
		else if(!this.isAiming && Perspective.FIRST_PERSON.equals(Perspective.current()) && this.doSwitchPerspective)
		{
			this.changePerspective(minecraft.options, Perspective.SHOULDER_SURFING);
		}
	}
	
	@SuppressWarnings("resource")
	public void changePerspective(Options options, Perspective perspective)
	{
		options.setCameraType(perspective.getCameraType());
		this.doShoulderSurfing = Perspective.SHOULDER_SURFING.equals(perspective);
	}
	
	public boolean doShoulderSurfing()
	{
		return this.doShoulderSurfing;
	}
	
	public void setShoulderSurfing(boolean doShoulderSurfing)
	{
		this.doShoulderSurfing = doShoulderSurfing;
	}
	
	public boolean isAiming()
	{
		return this.isAiming;
	}
	
	public static ShoulderInstance getInstance()
	{
		return instance;
	}
}
