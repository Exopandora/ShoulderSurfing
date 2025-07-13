package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class ShoulderSurfingRegistrar implements IShoulderSurfingRegistrar
{
	private static final ShoulderSurfingRegistrar INSTANCE = new ShoulderSurfingRegistrar();
	
	private final List<IAdaptiveItemCallback> adaptiveItemCallbacks = new ArrayList<IAdaptiveItemCallback>();
	private final List<ICameraCouplingCallback> cameraCouplingCallbacks = new ArrayList<ICameraCouplingCallback>();
	private final List<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = new ArrayList<ITargetCameraOffsetCallback>();
	private final List<ICameraEntityTransparencyCallback> cameraEntityTransparencyCallbacks = new ArrayList<ICameraEntityTransparencyCallback>();
	
	private boolean isFrozen;
	private PluginContext activePluginContext;
	
	private ShoulderSurfingRegistrar()
	{
		super();
	}
	
	@Override
	public IShoulderSurfingRegistrar registerAdaptiveItemCallback(IAdaptiveItemCallback adaptiveItemCallback)
	{
		this.checkState();
		IAdaptiveItemCallback wrapper = new AdaptiveItemCallbackWrapper(this.activePluginContext, adaptiveItemCallback);
		this.adaptiveItemCallbacks.add(wrapper);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraCouplingCallback(ICameraCouplingCallback cameraCouplingCallback)
	{
		this.checkState();
		ICameraCouplingCallback wrapper = new CameraCouplingCallbackWrapper(this.activePluginContext, cameraCouplingCallback);
		this.cameraCouplingCallbacks.add(wrapper);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerTargetCameraOffsetCallback(ITargetCameraOffsetCallback targetCameraOffsetCallback)
	{
		this.checkState();
		ITargetCameraOffsetCallback wrapper = new TargetCameraOffsetCallbackWrapper(this.activePluginContext, targetCameraOffsetCallback);
		this.targetCameraOffsetCallbacks.add(wrapper);
		return this;
	}
	
	@Override
	public IShoulderSurfingRegistrar registerCameraEntityTransparencyCallback(ICameraEntityTransparencyCallback cameraEntityTransparencyCallback)
	{
		this.checkState();
		ICameraEntityTransparencyCallback wrapper = new CameraEntityTransparencyCallbackWrapper(this.activePluginContext, cameraEntityTransparencyCallback);
		this.cameraEntityTransparencyCallbacks.add(wrapper);
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
	
	private record AdaptiveItemCallbackWrapper(PluginContext context, IAdaptiveItemCallback delegate) implements IAdaptiveItemCallback
	{
		@Override
		public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
		{
			return wrapCallback(this.context, () -> this.delegate.isHoldingAdaptiveItem(minecraft, entity));
		}
	}
	
	private record CameraCouplingCallbackWrapper(PluginContext context, ICameraCouplingCallback delegate) implements ICameraCouplingCallback
	{
		@Override
		public boolean isForcingCameraCoupling(Minecraft minecraft)
		{
			return wrapCallback(this.context, () -> this.delegate.isForcingCameraCoupling(minecraft));
		}
	}
	
	private record TargetCameraOffsetCallbackWrapper(PluginContext context, ITargetCameraOffsetCallback delegate) implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 pre(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
		{
			return wrapCallback(this.context, () -> this.delegate.pre(instance, targetOffset, defaultOffset));
		}
		
		@Override
		public Vec3 post(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
		{
			return wrapCallback(this.context, () -> this.delegate.post(instance, targetOffset, defaultOffset));
		}
	}
	
	private record CameraEntityTransparencyCallbackWrapper(PluginContext context, ICameraEntityTransparencyCallback delegate) implements ICameraEntityTransparencyCallback
	{
		@Override
		public float getCameraEntityAlpha(IShoulderSurfing instance, Entity cameraEntity, float partialTick)
		{
			return wrapCallback(this.context, () -> this.delegate.getCameraEntityAlpha(instance, cameraEntity, partialTick));
		}
	}
	
	private static <T> T wrapCallback(PluginContext context, Callable<T> callback)
	{
		try
		{
			return callback.call();
		}
		catch(Throwable t)
		{
			throw createExceptionWithContext(context, t);
		}
	}
	
	private static RuntimeException createExceptionWithContext(PluginContext context, Throwable t)
	{
		return new RuntimeException("Shoulder Surfing Reloaded encountered an unexpected error while trying to execute a callback for the plugin provided by " + context.formattedModName() + ". Please report this crash to " + context.formattedModName() + ".", t);
	}
	
	public static ShoulderSurfingRegistrar getInstance()
	{
		return INSTANCE;
	}
}
