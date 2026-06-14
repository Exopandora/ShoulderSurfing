package com.github.exopandora.shouldersurfing.util;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class SoundHelper {
	public static Vec3 calcCameraCentricSoundPosition(Entity entity) {
		IShoulderSurfing instance = IShoulderSurfing.getInstance();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vec3 leftVector = new Vec3(camera.leftVector());
		Vec3 effectiveOffset = instance.getCamera().getOffset().normalize().scale(instance.getCamera().getCameraDistance());
		return entity.position().add(leftVector.scale(effectiveOffset.x()));
	}
}
