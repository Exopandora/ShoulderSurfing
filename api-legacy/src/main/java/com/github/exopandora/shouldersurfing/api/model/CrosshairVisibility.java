package com.github.exopandora.shouldersurfing.api.model;

import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public enum CrosshairVisibility {
	ALWAYS,
	NEVER,
	WHEN_AIMING,
	WHEN_IN_RANGE,
	WHEN_AIMING_OR_IN_RANGE;
	
	public boolean doRender(@Nullable HitResult hitResult, boolean isAiming) {
		if (this == CrosshairVisibility.NEVER) {
			return false;
		} else if (this == CrosshairVisibility.WHEN_AIMING) {
			return isAiming;
		} else if (this == CrosshairVisibility.WHEN_IN_RANGE) {
			return hitResult != null && !HitResult.Type.MISS.equals(hitResult.getType());
		} else if (this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE) {
			return CrosshairVisibility.WHEN_IN_RANGE.doRender(hitResult, isAiming) || CrosshairVisibility.WHEN_AIMING.doRender(hitResult, isAiming);
		}
		return true;
	}
	
	public com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility toNewApi() {
		return switch (this) {
			case ALWAYS -> com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility.ALWAYS;
			case NEVER -> com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility.NEVER;
			case WHEN_AIMING -> com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility.WHEN_AIMING;
			case WHEN_IN_RANGE -> com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility.WHEN_IN_RANGE;
			case WHEN_AIMING_OR_IN_RANGE -> com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE;
		};
	}
	
	public static CrosshairVisibility fromNewApi(com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility crosshairVisibility) {
		return switch (crosshairVisibility) {
			case ALWAYS -> ALWAYS;
			case NEVER -> NEVER;
			case WHEN_AIMING -> WHEN_AIMING;
			case WHEN_IN_RANGE -> WHEN_IN_RANGE;
			case WHEN_AIMING_OR_IN_RANGE -> WHEN_AIMING_OR_IN_RANGE;
		};
	}
}
