package com.teamderpy.shouldersurfing.util;

import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;

public class ShoulderState
{
	private boolean enabled;
	private boolean switchPerspective;
	private boolean isAiming;
	
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected = null;
	
	private double cameraDistance;
	
	public boolean doSwitchPerspective()
	{
		return this.switchPerspective;
	}
	
	public void setSwitchPerspective(boolean switchPerspective)
	{
		this.switchPerspective = switchPerspective;
	}
	
	public Vec2f getLastTranslation()
	{
		return this.lastTranslation;
	}
	
	public void setLastTranslation(Vec2f lastTranslation)
	{
		this.lastTranslation = lastTranslation;
	}
	
	public Vec2f getTranslation()
	{
		return this.translation;
	}
	
	public void setTranslation(Vec2f translation)
	{
		this.translation = translation;
	}
	
	public Vec2f getProjected()
	{
		return this.projected;
	}
	
	public void setProjected(Vec2f projected)
	{
		this.projected = projected;
	}
	
	public boolean isAiming()
	{
		return this.isAiming;
	}
	
	public void setAiming(boolean isAiming)
	{
		this.isAiming = isAiming;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public void setCameraDistance(double cameraDistance)
	{
		this.cameraDistance = cameraDistance;
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean doShoulderSurfing()
	{
		return this.enabled && Minecraft.getMinecraft().gameSettings.thirdPersonView == Perspective.THIRD_PERSON_BACK.getPointOfView();
	}
}
