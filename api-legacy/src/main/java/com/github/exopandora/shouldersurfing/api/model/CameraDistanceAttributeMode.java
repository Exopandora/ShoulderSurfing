package com.github.exopandora.shouldersurfing.api.model;

public enum CameraDistanceAttributeMode {
	RELATIVE,
	ABSOLUTE,
	IGNORE;
	
	public com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode toNewApi() {
		return switch (this) {
			case RELATIVE -> com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode.RELATIVE;
			case ABSOLUTE -> com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode.ABSOLUTE;
			case IGNORE -> com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode.IGNORE;
		};
	}
	
	public static CameraDistanceAttributeMode fromNewApi(com.github.exopandora.shouldersurfing.api.client.CameraDistanceAttributeMode mods) {
		return switch (mods) {
			case RELATIVE -> RELATIVE;
			case ABSOLUTE -> ABSOLUTE;
			case IGNORE -> IGNORE;
		};
	}
}
