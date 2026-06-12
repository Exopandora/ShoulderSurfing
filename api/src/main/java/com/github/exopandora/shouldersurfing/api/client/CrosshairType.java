package com.github.exopandora.shouldersurfing.api.client;

public enum CrosshairType {
	ADAPTIVE,
	DYNAMIC,
	STATIC,
	STATIC_WITH_1PP,
	DYNAMIC_WITH_1PP;
	
	public boolean isAimingDecoupled() {
		return this == CrosshairType.STATIC || this == CrosshairType.STATIC_WITH_1PP;
	}
}
