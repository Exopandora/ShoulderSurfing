package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ShoulderSurfingRegistrar implements IShoulderSurfingRegistrar
{
	private static final ShoulderSurfingRegistrar INSTANCE = new ShoulderSurfingRegistrar();
	
	private final List<IAdaptiveItemCallback> adaptiveItemCallbacks = new LinkedList<IAdaptiveItemCallback>();
	private final List<ICameraCouplingCallback> cameraCouplingCallbacks = new LinkedList<ICameraCouplingCallback>();
	private final List<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = new LinkedList<ITargetCameraOffsetCallback>();
	private final List<ICameraEntityTransparencyCallback> cameraEntityTransparencyCallbacks = new LinkedList<ICameraEntityTransparencyCallback>();
	private final List<ITickableCallback> tickableCallbacks = new LinkedList<ITickableCallback>();
	private final List<IPlayerStateCallback> playerStateCallbacks = new LinkedList<IPlayerStateCallback>();
	private final List<ICameraRotationSetupCallback> cameraRotationSetupCallbacks = new LinkedList<ICameraRotationSetupCallback>();
	
	private boolean isFrozen;
	private PluginContext activePluginContext;
	
	private ShoulderSurfingRegistrar()
	{
		super();
	}
	
	@Override
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback)
	{
		return this.registerCallback(this.adaptiveItemCallbacks, adaptiveItemCallback, IAdaptiveItemCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback)
	{
		return this.registerCallback(this.cameraCouplingCallbacks, cameraCouplingCallback, ICameraCouplingCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback)
	{
		return this.registerCallback(this.targetCameraOffsetCallbacks, targetCameraOffsetCallback, ITargetCameraOffsetCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback cameraEntityTransparencyCallback)
	{
		return this.registerCallback(this.cameraEntityTransparencyCallbacks, cameraEntityTransparencyCallback, ICameraEntityTransparencyCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerPlayerStateCallback(IPlayerStateCallback callback)
	{
		return this.registerCallback(this.playerStateCallbacks, callback, IPlayerStateCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraRotationSetupCallback(ICameraRotationSetupCallback callback)
	{
		return this.registerCallback(this.cameraRotationSetupCallbacks, callback, ICameraRotationSetupCallback.class);
	}
	
	private <T> IShoulderSurfingRegistrar registerCallback(List<T> registry, T callback, Class<T> klass)
	{
		this.checkState();
		T proxy = createProxy(this.activePluginContext, callback, klass);
		registry.add(proxy);
		this.registerTickableCallback(proxy);
		return this;
	}
	
	private void checkState()
	{
		if(this.isFrozen)
		{
			throw new IllegalStateException("Unable to register plugins outside plugin loading stage");
		}
		
		if(this.activePluginContext == null)
		{
			throw new IllegalStateException("No active plugin context");
		}
	}
	
	private <T> void registerTickableCallback(T callback)
	{
		if(callback instanceof ITickableCallback tickableCallback)
		{
			this.tickableCallbacks.add(tickableCallback);
		}
	}
	
	void setPluginContext(PluginContext context)
	{
		this.activePluginContext = context;
	}
	
	protected void freeze()
	{
		this.isFrozen = true;
		this.activePluginContext = null;
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
	
	public List<ICameraEntityTransparencyCallback> getCameraEntityTransparencyCallbacks()
	{
		return Collections.unmodifiableList(this.cameraEntityTransparencyCallbacks);
	}
	
	public List<ITickableCallback> getTickableCallbacks()
	{
		return Collections.unmodifiableList(this.tickableCallbacks);
	}
	
	public List<IPlayerStateCallback> getPlayerStateCallbacks()
	{
		return Collections.unmodifiableList(this.playerStateCallbacks);
	}
	
	public List<ICameraRotationSetupCallback> getSetupCameraRotationCallbacks()
	{
		return Collections.unmodifiableList(this.cameraRotationSetupCallbacks);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T createProxy(PluginContext context, T callback, Class<T> klass)
	{
		ClassLoader classLoader = ShoulderSurfingRegistrar.class.getClassLoader();
		Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>(1);
		interfaces.add(klass);
		
		if(callback instanceof ITickableCallback)
		{
			interfaces.add(ITickableCallback.class);
		}
		
		CallbackInvocationHandler<T> invocationHandler = new CallbackInvocationHandler<T>(context, callback, interfaces);
		return (T) Proxy.newProxyInstance(classLoader, interfaces.toArray(Class<?>[]::new), invocationHandler);
	}
	
	public static ShoulderSurfingRegistrar getInstance()
	{
		return INSTANCE;
	}
}
