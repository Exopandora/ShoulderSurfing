package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
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
	
	public Perspective next(IClientConfig config)
	{
		Perspective next;
		
		if(config.replaceDefaultPerspective())
		{
			switch(this)
			{
				case FIRST_PERSON:
				case THIRD_PERSON_BACK:
					next = SHOULDER_SURFING;
					break;
				case THIRD_PERSON_FRONT:
					next = FIRST_PERSON;
					break;
				case SHOULDER_SURFING:
					next = THIRD_PERSON_FRONT;
					break;
				default:
					throw new IllegalArgumentException();
			}
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
		switch(this)
		{
			case FIRST_PERSON:
				return config.isFirstPersonEnabled();
			case THIRD_PERSON_BACK:
				return config.isThirdPersonBackEnabled() && !config.replaceDefaultPerspective();
			case THIRD_PERSON_FRONT:
				return config.isThirdPersonFrontEnabled();
			case SHOULDER_SURFING:
				return true;
			default:
				throw new IllegalArgumentException();
		}
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
