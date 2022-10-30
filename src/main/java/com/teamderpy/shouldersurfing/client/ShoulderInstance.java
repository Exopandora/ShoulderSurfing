package com.teamderpy.shouldersurfing.client;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class ShoulderInstance
{
	private static final ShoulderInstance INSTANCE = new ShoulderInstance();
	private boolean doShoulderSurfing;
	private boolean doSwitchPerspective;
	private boolean isAiming;
	private int thirdPersonView = Config.CLIENT.getDefaultPerspective().getPointOfView();
	
	public void tick()
	{
		if(this.thirdPersonView != Minecraft.getMinecraft().gameSettings.thirdPersonView)
		{
			this.doShoulderSurfing = Config.CLIENT.replaceDefaultPerspective() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 1;
		}
		
		if(!Perspective.FIRST_PERSON.equals(Perspective.current()))
		{
			this.doSwitchPerspective = false;
		}
		
		this.isAiming = ShoulderHelper.isHoldingSpecialItem();
		
		if(this.isAiming && Config.CLIENT.getCrosshairType().doSwitchPerspective() && this.doShoulderSurfing)
		{
			this.changePerspective(Perspective.FIRST_PERSON);
			this.doSwitchPerspective = true;
		}
		else if(!this.isAiming && Perspective.FIRST_PERSON.equals(Perspective.current()) && this.doSwitchPerspective)
		{
			this.changePerspective(Perspective.SHOULDER_SURFING);
		}
	}
	
	public void changePerspective(Perspective perspective)
	{
		Minecraft.getMinecraft().gameSettings.thirdPersonView = perspective.getPointOfView();
		this.thirdPersonView = perspective.getPointOfView();
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
		return INSTANCE;
	}
}
