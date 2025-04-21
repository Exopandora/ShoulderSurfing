package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoulderSurfingRegistrar implements IShoulderSurfingRegistrar
{
	private static final ShoulderSurfingRegistrar INSTANCE = new ShoulderSurfingRegistrar();
	
	private final List<IAdaptiveItemCallback> adaptiveItemCallbacks = new ArrayList<IAdaptiveItemCallback>();
	private final List<ICameraCouplingCallback> cameraCouplingCallbacks = new ArrayList<ICameraCouplingCallback>();
	private final List<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = new ArrayList<ITargetCameraOffsetCallback>();
	
	private ShoulderSurfingRegistrar()
	{
		super();
	}
	
	@Override
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback)
	{
		this.adaptiveItemCallbacks.add(adaptiveItemCallback);
		return this;
	}

	@Override
	public IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback) {
		this.cameraCouplingCallbacks.add(cameraCouplingCallback);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback)
	{
		this.targetCameraOffsetCallbacks.add(targetCameraOffsetCallback);
		return this;
	}
	
	public List<IAdaptiveItemCallback> getAdaptiveItemCallbacks()
	{
		return Collections.unmodifiableList(this.adaptiveItemCallbacks);
	}
	
	public List<ICameraCouplingCallback> getCameraCouplingCallbacks()
	{
		return Collections.unmodifiableList(this.cameraCouplingCallbacks);
	}
	
	public List<ITargetCameraOffsetCallback> getTargetCameraOffsetCallbacks()
	{
		return Collections.unmodifiableList(this.targetCameraOffsetCallbacks);
	}
	
	public static ShoulderSurfingRegistrar getInstance()
	{
		return INSTANCE;
	}
}
