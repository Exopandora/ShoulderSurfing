package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
				next = Perspective.SHOULDER_SURFING;
			}
			else if(this == Perspective.SHOULDER_SURFING)
			{
				next = Perspective.THIRD_PERSON_FRONT;
			}
			else if(this == Perspective.THIRD_PERSON_FRONT)
			{
				next = Perspective.FIRST_PERSON;
			}
		}
		
		if(Config.CLIENT.skipThirdPersonFront() && next == Perspective.THIRD_PERSON_FRONT)
		{
			return next.next();
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
