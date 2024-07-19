package com.github.exopandora.shouldersurfing.api.client;

import java.util.ServiceLoader;

public class ShoulderSurfing
{
	private static final IShoulderSurfing INSTANCE = ServiceLoader.load(IShoulderSurfing.class).iterator().next();
	
	public static IShoulderSurfing getInstance()
	{
		return INSTANCE;
	}
}
