package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
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
	
	public Perspective next(IClientConfig config)
	{
		Perspective next;
		
		if(config.replaceDefaultPerspective())
		{
			next = switch(this)
			{
				case FIRST_PERSON, THIRD_PERSON_BACK -> SHOULDER_SURFING;
				case THIRD_PERSON_FRONT -> FIRST_PERSON;
				case SHOULDER_SURFING -> THIRD_PERSON_FRONT;
			};
		}
		else
		{
			next = Perspective.values()[(this.ordinal() + 1) % Perspective.values().length];
		}
		
		switch(next)
		{
			case FIRST_PERSON:
				if(config.isFirstPersonEnabled())
				{
					return FIRST_PERSON;
				}
				break;
			case THIRD_PERSON_BACK:
				if(config.isThirdPersonBackEnabled())
				{
					return THIRD_PERSON_BACK;
				}
				break;
			case THIRD_PERSON_FRONT:
				if(config.isThirdPersonFrontEnabled())
				{
					return THIRD_PERSON_FRONT;
				}
				break;
			case SHOULDER_SURFING:
				return SHOULDER_SURFING;
		}
		
		return next.next(config);
	}
	
	public boolean isEnabled(IClientConfig config)
	{
		return switch(this)
		{
			case FIRST_PERSON -> config.isFirstPersonEnabled();
			case THIRD_PERSON_BACK -> config.isThirdPersonBackEnabled() && !config.replaceDefaultPerspective();
			case THIRD_PERSON_FRONT -> config.isThirdPersonFrontEnabled();
			case SHOULDER_SURFING -> true;
		};
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
