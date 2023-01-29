package com.teamderpy.shouldersurfing.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.teamderpy.shouldersurfing.api.IShoulderSurfingRegistrar;
import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;

public class ShoulderSurfingRegistrar implements IShoulderSurfingRegistrar
{
	private static final ShoulderSurfingRegistrar INSTANCE = new ShoulderSurfingRegistrar();
	
	private final List<IAdaptiveItemCallback> adaptiveItemCallbacks = new ArrayList<IAdaptiveItemCallback>();
	
	private ShoulderSurfingRegistrar()
	{
		super();
	}
	
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback)
	{
		this.adaptiveItemCallbacks.add(adaptiveItemCallback);
		return this;
	}
	
	public List<IAdaptiveItemCallback> getAdaptiveItemCallbacks()
	{
		return Collections.unmodifiableList(this.adaptiveItemCallbacks);
	}
	
	public static ShoulderSurfingRegistrar getInstance()
	{
		return INSTANCE;
	}
}
