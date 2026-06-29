package com.github.exopandora.shouldersurfing.api.model;

public enum ViewBobbingMode {
	INHERIT,
	ON,
	OFF;
	
	public com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode toNewApi() {
		return switch (this) {
			case INHERIT -> com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode.INHERIT;
			case ON -> com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode.ON;
			case OFF -> com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode.OFF;
		};
	}
	
	public static ViewBobbingMode fromNewApi(com.github.exopandora.shouldersurfing.api.client.ViewBobbingMode perspective) {
		return switch (perspective) {
			case INHERIT -> INHERIT;
			case ON -> ON;
			case OFF -> OFF;
		};
	}
}
