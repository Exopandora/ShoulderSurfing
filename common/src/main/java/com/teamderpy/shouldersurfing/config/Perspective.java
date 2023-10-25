package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public enum Perspective
{
	FIRST_PERSON(CameraType.FIRST_PERSON, CrosshairVisibility.ALWAYS),
	THIRD_PERSON_BACK(CameraType.THIRD_PERSON_BACK, CrosshairVisibility.NEVER),
	THIRD_PERSON_FRONT(CameraType.THIRD_PERSON_FRONT, CrosshairVisibility.NEVER),
	SHOULDER_SURFING(CameraType.THIRD_PERSON_BACK, CrosshairVisibility.ALWAYS);
	
	private final CameraType cameraType;
	private final CrosshairVisibility defaultCrosshairVisibility;
	
	private Perspective(CameraType cameraType, CrosshairVisibility defaultCrosshairVisibility)
	{
		this.cameraType = cameraType;
		this.defaultCrosshairVisibility = defaultCrosshairVisibility;
	}
	
	public CameraType getCameraType()
	{
		return this.cameraType;
	}
	
	public CrosshairVisibility getDefaultCrosshairVisibility()
	{
		return this.defaultCrosshairVisibility;
	}
	
	public Perspective next()
	{
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
		
		return Perspective.values()[(this.ordinal() + 1) % Perspective.values().length];
	}
	
	public static Perspective of(CameraType cameraType, boolean shoulderSurfing)
	{
		switch(cameraType)
		{
			case FIRST_PERSON:
				return Perspective.FIRST_PERSON;
			case THIRD_PERSON_BACK:
				return shoulderSurfing ? Perspective.SHOULDER_SURFING : Perspective.THIRD_PERSON_BACK;
			case THIRD_PERSON_FRONT:
				return Perspective.THIRD_PERSON_FRONT;
			default:
				return Perspective.FIRST_PERSON;
		}
	}
	
	@SuppressWarnings("resource")
	public static Perspective current()
	{
		return Perspective.of(Minecraft.getInstance().options.getCameraType(), ShoulderInstance.getInstance().doShoulderSurfing());
	}
}
