package com.github.exopandora.shouldersurfing.api;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;

import java.util.ServiceLoader;

public class ShoulderSurfing {
	private static final IShoulderSurfing INSTANCE = ServiceLoader.load(IShoulderSurfing.class).findFirst().orElseThrow();
	
	public static IShoulderSurfing getInstance() {
		return INSTANCE;
	}
}
