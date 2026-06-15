package com.github.exopandora.shouldersurfing.api.client;

public enum CrosshairType {
	ADAPTIVE(false),
	DYNAMIC(false),
	STATIC(true),
	STATIC_WITH_1PP(true),
	DYNAMIC_WITH_1PP(false);
	
	private final boolean isAimingDecoupled;
	
	CrosshairType(boolean isAimingDecoupled) {
		this.isAimingDecoupled = isAimingDecoupled;
	}
	
	public boolean isAimingDecoupled() {
		return this.isAimingDecoupled;
	}
}
