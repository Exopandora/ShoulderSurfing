package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.legacy.adapter.ShoulderSurfingLegacyAdapter;

public class ShoulderSurfing {
	private static final IShoulderSurfing ADAPTER = new ShoulderSurfingLegacyAdapter();
	
	public static IShoulderSurfing getInstance() {
		return ADAPTER;
	}
}
