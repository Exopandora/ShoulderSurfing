package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
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
	
	Perspective(CameraType cameraType, CrosshairVisibility defaultCrosshairVisibility)
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
	
	public Perspective next(boolean replaceDefaultPerspective, boolean skipThirdPersonFront)
	{
		Perspective next = Perspective.values()[(this.ordinal() + 1) % Perspective.values().length];
		
		if(replaceDefaultPerspective)
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
		
		if(skipThirdPersonFront && next == Perspective.THIRD_PERSON_FRONT)
		{
			return next.next(replaceDefaultPerspective, true);
		}
		
		return next;
	}
	
	public static Perspective of(CameraType cameraType, boolean shoulderSurfing)
	{
		return switch(cameraType)
		{
			case FIRST_PERSON -> Perspective.FIRST_PERSON;
			case THIRD_PERSON_BACK -> shoulderSurfing ? Perspective.SHOULDER_SURFING : Perspective.THIRD_PERSON_BACK;
			case THIRD_PERSON_FRONT -> Perspective.THIRD_PERSON_FRONT;
		};
	}
	
	public static Perspective current()
	{
		return Perspective.of(Minecraft.getInstance().options.getCameraType(), ShoulderSurfing.getInstance().isShoulderSurfing());
	}
}
