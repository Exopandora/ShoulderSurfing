package com.github.exopandora.shouldersurfing;

public class ShoulderSurfingAPI {
	private static Boolean forceCoupling = false;

	public static Boolean getForcedCoupledCamera() {
		return forceCoupling;
	}

	public static void setForcedCoupledCamera(Boolean value) {
		forceCoupling = value;
	}
}
