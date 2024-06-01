package com.github.exopandora.shouldersurfing.api.client;

import net.minecraft.util.math.vector.Vector3d;

public interface IShoulderSurfingCamera
{
	double getCameraDistance();
	
	Vector3d getOffset();
	Vector3d getRenderOffset();
	Vector3d getTargetOffset();
	
	float getXRot();
	void setXRot(float xRot);
	
	float getYRot();
	void setYRot(float yRot);
}
