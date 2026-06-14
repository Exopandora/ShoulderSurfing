package com.github.exopandora.shouldersurfing.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class Util {
	public static boolean isImprovedTransparencyEnabled() {
		Minecraft instance = Minecraft.getInstance();
		//noinspection ConstantValue
		if (instance != null && instance.options != null) {
			return instance.options.improvedTransparency().get();
		}
		return false;
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isCameraEntityRidingBoat() {
		Minecraft instance = Minecraft.getInstance();
		//noinspection ConstantValue
		if (instance != null && instance.gameRenderer != null && instance.gameRenderer.getMainCamera() != null) {
			return instance.getCameraEntity() != null && instance.getCameraEntity().getVehicle() instanceof AbstractBoat;
		}
		return false;
	}
}
