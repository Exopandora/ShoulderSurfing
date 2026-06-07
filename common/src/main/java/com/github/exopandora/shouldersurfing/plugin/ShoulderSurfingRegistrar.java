package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback;
import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;

import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ShoulderSurfingRegistrar implements IShoulderSurfingRegistrar {
	private static final ShoulderSurfingRegistrar INSTANCE = new ShoulderSurfingRegistrar();
	
	private final CallbackRegistry<IAdaptiveItemCallback> adaptiveItemCallbacks = new CallbackRegistry<IAdaptiveItemCallback>();
	private final CallbackRegistry<ICameraCouplingCallback> cameraCouplingCallbacks = new CallbackRegistry<ICameraCouplingCallback>();
	private final CallbackRegistry<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = new CallbackRegistry<ITargetCameraOffsetCallback>();
	private final CallbackRegistry<ICameraEntityTransparencyCallback> cameraEntityTransparencyCallbacks = new CallbackRegistry<ICameraEntityTransparencyCallback>();
	private final CallbackRegistry<ITickableCallback> tickableCallbacks = new CallbackRegistry<ITickableCallback>();
	private final CallbackRegistry<IPlayerStateCallback> playerStateCallbacks = new CallbackRegistry<IPlayerStateCallback>();
	private final CallbackRegistry<ICameraRotationSetupCallback> cameraRotationSetupCallbacks = new CallbackRegistry<ICameraRotationSetupCallback>();
	private final CallbackRegistry<IPlayerInputCallback> playerInputCallbacks = new CallbackRegistry<IPlayerInputCallback>();
	
	private boolean isFrozen;
	private PluginContext<?> activePluginContext;
	
	private ShoulderSurfingRegistrar() {
		super();
	}
	
	@Override
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(int priority, IAdaptiveItemCallback callback) {
		return this.registerCallback(this.adaptiveItemCallbacks, priority, callback, IAdaptiveItemCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraCouplingCallback(int priority, ICameraCouplingCallback callback) {
		return this.registerCallback(this.cameraCouplingCallbacks, priority, callback, ICameraCouplingCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(int priority, ITargetCameraOffsetCallback callback) {
		return this.registerCallback(this.targetCameraOffsetCallbacks, priority, callback, ITargetCameraOffsetCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(int priority, ICameraEntityTransparencyCallback callback) {
		return this.registerCallback(this.cameraEntityTransparencyCallbacks, priority, callback, ICameraEntityTransparencyCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerPlayerStateCallback(int priority, IPlayerStateCallback callback) {
		return this.registerCallback(this.playerStateCallbacks, priority, callback, IPlayerStateCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraRotationSetupCallback(int priority, ICameraRotationSetupCallback callback) {
		return this.registerCallback(this.cameraRotationSetupCallbacks, priority, callback, ICameraRotationSetupCallback.class);
	}
	
	@Override
	public IShoulderSurfingRegistrar registerPlayerInputCallback(int priority, IPlayerInputCallback callback) {
		return this.registerCallback(this.playerInputCallbacks, priority, callback, IPlayerInputCallback.class);
	}
	
	private <T> IShoulderSurfingRegistrar registerCallback(CallbackRegistry<T> registry, int priority, T callback, Class<T> klass) {
		this.checkState();
		T proxy = createProxy(this.activePluginContext, callback, klass);
		registry.register(priority, proxy);
		this.registerTickableCallback(priority, proxy);
		return this;
	}
	
	private void checkState() {
		if (this.isFrozen) {
			throw new IllegalStateException("Unable to register plugins outside plugin loading stage");
		}
		if (this.activePluginContext == null) {
			throw new IllegalStateException("No active plugin context");
		}
	}
	
	private <T> void registerTickableCallback(int priority, T callback) {
		if (callback instanceof ITickableCallback tickableCallback) {
			this.tickableCallbacks.register(priority, tickableCallback);
		}
	}
	
	void setPluginContext(PluginContext<?> context) {
		this.activePluginContext = context;
	}
	
	protected void freeze() {
		this.isFrozen = true;
		this.activePluginContext = null;
	}
	
	public List<IAdaptiveItemCallback> getAdaptiveItemCallbacks() {
		return this.adaptiveItemCallbacks.getCallbacks();
	}
	
	public List<ICameraCouplingCallback> getCameraCouplingCallbacks() {
		return this.cameraCouplingCallbacks.getCallbacks();
	}
	
	public List<ITargetCameraOffsetCallback> getTargetCameraOffsetCallbacks() {
		return this.targetCameraOffsetCallbacks.getCallbacks();
	}
	
	public List<ICameraEntityTransparencyCallback> getCameraEntityTransparencyCallbacks() {
		return this.cameraEntityTransparencyCallbacks.getCallbacks();
	}
	
	public List<ITickableCallback> getTickableCallbacks() {
		return this.tickableCallbacks.getCallbacks();
	}
	
	public List<IPlayerStateCallback> getPlayerStateCallbacks() {
		return this.playerStateCallbacks.getCallbacks();
	}
	
	public List<ICameraRotationSetupCallback> getSetupCameraRotationCallbacks() {
		return this.cameraRotationSetupCallbacks.getCallbacks();
	}
	
	public List<IPlayerInputCallback> getPlayerInputCallbacks() {
		return this.playerInputCallbacks.getCallbacks();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T createProxy(PluginContext<?> context, T callback, Class<T> klass) {
		ClassLoader classLoader = ShoulderSurfingRegistrar.class.getClassLoader();
		Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>(1);
		interfaces.add(klass);
		if (callback instanceof ITickableCallback) {
			interfaces.add(ITickableCallback.class);
		}
		CallbackInvocationHandler<T> invocationHandler = new CallbackInvocationHandler<T>(context, callback, interfaces);
		return (T) Proxy.newProxyInstance(classLoader, interfaces.toArray(Class<?>[]::new), invocationHandler);
	}
	
	public static ShoulderSurfingRegistrar getInstance() {
		return INSTANCE;
	}
}
