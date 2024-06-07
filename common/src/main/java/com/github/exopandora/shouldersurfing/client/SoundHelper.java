package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.accessors.ActiveRenderInfoAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class SoundHelper
{
	public static Vector3d calcCameraCentricSoundPosition(Entity entity)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		ActiveRenderInfoAccessor camera = (ActiveRenderInfoAccessor) Minecraft.getInstance().gameRenderer.getMainCamera();
		Vector3d leftVector = new Vector3d(camera.getLeft());
		Vector3d effectiveOffset = instance.getCamera().getOffset().normalize().scale(instance.getCamera().getCameraDistance());
		return entity.position().add(leftVector.scale(effectiveOffset.x()));
	}
}
