package com.github.exopandora.shouldersurfing.api.client;

import net.minecraft.world.phys.Vec3;

public interface IShoulderSurfingCamera
{
	double getCameraDistance();
	
	Vec3 getOffset();
	
	Vec3 getRenderOffset();
	
	Vec3 getTargetOffset();
	
	float getXRot();
	
	void setXRot(float xRot);
	
	float getYRot();
	
	void setYRot(float yRot);
}
