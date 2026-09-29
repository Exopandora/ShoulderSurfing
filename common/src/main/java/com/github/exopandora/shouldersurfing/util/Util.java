package com.github.exopandora.shouldersurfing.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class Util {
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isCameraEntityRidingBoat() {
		var instance = Minecraft.getInstance();
		//noinspection ConstantValue
		return instance != null && instance.gameRenderer != null && instance.gameRenderer.mainCamera() != null
			&& instance.getCameraEntity() != null && instance.getCameraEntity().getVehicle() instanceof AbstractBoat;
	}
}
