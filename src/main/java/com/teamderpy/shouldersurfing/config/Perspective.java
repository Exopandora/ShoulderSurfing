package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public enum Perspective
{
	FIRST_PERSON(0, CrosshairVisibility.ALWAYS),
	THIRD_PERSON_BACK(1, CrosshairVisibility.NEVER),
	THIRD_PERSON_FRONT(2, CrosshairVisibility.NEVER),
	SHOULDER_SURFING(1, CrosshairVisibility.ALWAYS);
	
	private final int pointOfView;
	private final CrosshairVisibility defaultCrosshairVisibility;
	
	private Perspective(int pointOfView, CrosshairVisibility defaultCrosshairVisibility)
	{
		this.pointOfView = pointOfView;
		this.defaultCrosshairVisibility = defaultCrosshairVisibility;
	}
	
	public int getPointOfView()
	{
		return this.pointOfView;
	}
	
	public CrosshairVisibility getDefaultCrosshairVisibility()
	{
		return this.defaultCrosshairVisibility;
	}
	
	public Perspective next()
	{
		Perspective next = Perspective.values()[(this.ordinal() + 1) % Perspective.values().length];
		
		if(Config.CLIENT.replaceDefaultPerspective())
		{
			if(this == Perspective.FIRST_PERSON)
			{
				return Perspective.SHOULDER_SURFING;
			}
			else if(this == Perspective.SHOULDER_SURFING)
			{
				return Perspective.THIRD_PERSON_FRONT;
			}
			else if(this == Perspective.THIRD_PERSON_FRONT)
			{
				return Perspective.FIRST_PERSON;
			}
		}
		
		return next;
	}
	
	public static Perspective of(int pointOfView, boolean shoulderSurfing)
	{
		switch(pointOfView)
		{
			case 0:
				return Perspective.FIRST_PERSON;
			case 1:
				return shoulderSurfing ? Perspective.SHOULDER_SURFING : Perspective.THIRD_PERSON_BACK;
			case 2:
				return Perspective.THIRD_PERSON_FRONT;
			default:
				return Perspective.FIRST_PERSON;
		}
	}
	
	public static Perspective current()
	{
		return Perspective.of(Minecraft.getMinecraft().gameSettings.thirdPersonView, ShoulderInstance.getInstance().doShoulderSurfing());
	}
}
