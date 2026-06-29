package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public enum CrosshairType {
	ADAPTIVE,
	DYNAMIC,
	STATIC,
	STATIC_WITH_1PP,
	DYNAMIC_WITH_1PP;
	
	public boolean isDynamic(Entity entity, boolean isAiming) {
		if (this == CrosshairType.ADAPTIVE) {
			return isAiming;
		} else if (this == CrosshairType.DYNAMIC || this == CrosshairType.DYNAMIC_WITH_1PP) {
			return !(entity instanceof Player player && player.isScoping());
		}
		return false;
	}
	
	public boolean isAimingDecoupled() {
		return this == CrosshairType.STATIC || this == CrosshairType.STATIC_WITH_1PP;
	}
	
	public boolean doSwitchPerspective(boolean isAiming) {
		if (this == CrosshairType.STATIC_WITH_1PP || this == CrosshairType.DYNAMIC_WITH_1PP) {
			return isAiming;
		}
		return false;
	}
	
	public com.github.exopandora.shouldersurfing.api.client.CrosshairType toNewApi() {
		return switch (this) {
			case ADAPTIVE -> com.github.exopandora.shouldersurfing.api.client.CrosshairType.ADAPTIVE;
			case DYNAMIC -> com.github.exopandora.shouldersurfing.api.client.CrosshairType.DYNAMIC;
			case STATIC -> com.github.exopandora.shouldersurfing.api.client.CrosshairType.STATIC;
			case STATIC_WITH_1PP -> com.github.exopandora.shouldersurfing.api.client.CrosshairType.STATIC_WITH_1PP;
			case DYNAMIC_WITH_1PP -> com.github.exopandora.shouldersurfing.api.client.CrosshairType.DYNAMIC_WITH_1PP;
		};
	}
	
	public static CrosshairType fromNewApi(com.github.exopandora.shouldersurfing.api.client.CrosshairType crosshairType) {
		return switch (crosshairType) {
			case ADAPTIVE -> ADAPTIVE;
			case DYNAMIC -> DYNAMIC;
			case STATIC -> STATIC;
			case STATIC_WITH_1PP -> STATIC_WITH_1PP;
			case DYNAMIC_WITH_1PP -> DYNAMIC_WITH_1PP;
		};
	}
}
