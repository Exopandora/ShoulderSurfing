package com.github.exopandora.shouldersurfing.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class Util {
	public static boolean isImprovedTransparencyEnabled() {
		Minecraft instance = Minecraft.getInstance();
		//noinspection ConstantValue
		return instance != null && instance.options != null && instance.options.improvedTransparency().get();
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isCameraEntityRidingBoat() {
		Minecraft instance = Minecraft.getInstance();
		//noinspection ConstantValue
		return instance != null && instance.gameRenderer != null && instance.gameRenderer.getMainCamera() != null &&
			instance.getCameraEntity() != null && instance.getCameraEntity().getVehicle() instanceof AbstractBoat;
	}
}
