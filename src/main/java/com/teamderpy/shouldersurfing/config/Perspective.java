package com.teamderpy.shouldersurfing.config;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Perspective
{
	FIRST_PERSON(PointOfView.FIRST_PERSON, CrosshairVisibility.ALWAYS),
	THIRD_PERSON_BACK(PointOfView.THIRD_PERSON_BACK, CrosshairVisibility.NEVER),
	THIRD_PERSON_FRONT(PointOfView.THIRD_PERSON_FRONT, CrosshairVisibility.NEVER),
	SHOULDER_SURFING(PointOfView.THIRD_PERSON_BACK, CrosshairVisibility.ALWAYS);
	
	private final PointOfView pointOfView;
	private final CrosshairVisibility defaultCrosshairVisibility;
	
	private Perspective(PointOfView pointOfView, CrosshairVisibility defaultCrosshairVisibility)
	{
		this.pointOfView = pointOfView;
		this.defaultCrosshairVisibility = defaultCrosshairVisibility;
	}
	
	public PointOfView getPointOfView()
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
	
	public static Perspective of(PointOfView pointOfView, boolean shoulderSurfing)
	{
		switch(pointOfView)
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
	
	public static Perspective current()
	{
		return Perspective.of(Minecraft.getInstance().options.getCameraType(), ShoulderSurfing.STATE.doShoulderSurfing());
	}
}
