package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.Minecraft;

public enum Perspective
{
	FIRST_PERSON(PointOfView.FIRST_PERSON, CrosshairVisibility.ALWAYS),
	THIRD_PERSON_BACK(PointOfView.THIRD_PERSON_BACK, CrosshairVisibility.NEVER),
	THIRD_PERSON_FRONT(PointOfView.THIRD_PERSON_FRONT, CrosshairVisibility.NEVER),
	SHOULDER_SURFING(PointOfView.THIRD_PERSON_BACK, CrosshairVisibility.ALWAYS);
	
	private final PointOfView cameraType;
	private final CrosshairVisibility defaultCrosshairVisibility;
	
	Perspective(PointOfView cameraType, CrosshairVisibility defaultCrosshairVisibility)
	{
		this.cameraType = cameraType;
		this.defaultCrosshairVisibility = defaultCrosshairVisibility;
	}
	
	public PointOfView getCameraType()
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
	
	public static Perspective of(PointOfView cameraType, boolean shoulderSurfing)
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
				throw new IllegalArgumentException();
		}
	}
	
	public static Perspective current()
	{
		return Perspective.of(Minecraft.getInstance().options.getCameraType(), ShoulderSurfing.getInstance().isShoulderSurfing());
	}
}
