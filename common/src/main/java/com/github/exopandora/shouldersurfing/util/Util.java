package com.github.exopandora.shouldersurfing.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Util {
	public static boolean isImprovedTransparencyEnabled() {
		var instance = Minecraft.getInstance();
		//noinspection ConstantValue
		if (instance != null && instance.options != null) {
			return instance.options.improvedTransparency().get();
		}
		return false;
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isCameraEntityRidingBoat() {
		var instance = Minecraft.getInstance();
		//noinspection ConstantValue
		if (instance != null && instance.gameRenderer != null && instance.gameRenderer.getMainCamera() != null) {
			return instance.getCameraEntity() != null && instance.getCameraEntity().getVehicle() instanceof AbstractBoat;
		}
		return false;
	}
	
	public static Predicate<String> expressionToMatchPredicate(String expression) {
		try {
			return Pattern.compile(expression).asMatchPredicate();
		} catch (Exception e) {
			return expression::equals;
		}
	}
}
