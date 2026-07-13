package com.github.exopandora.shouldersurfing.util;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SoundHelper {
	public static Vec3 calcCameraCentricSoundPosition(Entity entity) {
		var instance = IShoulderSurfing.getInstance();
		var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		var leftVector = new Vec3(camera.leftVector());
		var effectiveOffset = instance.getCamera().getOffset().normalize().scale(instance.getCamera().getCameraDistance());
		return entity.position().add(leftVector.scale(effectiveOffset.x()));
	}
}
