package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.plugin.CreateModTargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.callbacks.AdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.CameraEntityTransparencyCallback;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.StringUtils;

public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		registrar.registerAdaptiveItemCallback(new AdaptiveItemCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallbackWhenAiming());
		registerCompatibilityCallback(Mods.CREATE, () -> registrar.registerTargetCameraOffsetCallback(new CreateModTargetCameraOffsetCallback()));
	}
	
	private static void registerCompatibilityCallback(Mods mod, Runnable runnable)
	{
		if(mod.isLoaded())
		{
			String modName = StringUtils.capitalize(mod.name());
			
			try
			{
				ShoulderSurfingCommon.LOGGER.info("Registering compatibility callback for {}", modName);
				runnable.run();
			}
			catch(Throwable t)
			{
				ShoulderSurfingCommon.LOGGER.error("Failed to load compatibility callback for {}", modName, t);
			}
		}
	}
	
	private static class CameraEntityTransparencyCallbackWhenAiming implements ICameraEntityTransparencyCallback, ITickableCallback
	{
		private static final int TRANSITION_TICK_COUNT = 5;
		private int aimingTicks;
		private int aimingTicksO;
		
		@Override
		public void tick()
		{
			if(Config.CLIENT.turnPlayerTransparentWhenAiming())
			{
				this.aimingTicksO = aimingTicks;
				
				if(ShoulderSurfingImpl.getInstance().isAiming())
				{
					if(this.aimingTicks < TRANSITION_TICK_COUNT)
					{
						this.aimingTicks++;
					}
				}
				else if(this.aimingTicks > 0)
				{
					this.aimingTicks--;
				}
			}
		}
		
		@Override
		public float getCameraEntityAlpha(IShoulderSurfing instance, Entity entity, float partialTick)
		{
			if(Config.CLIENT.turnPlayerTransparentWhenAiming())
			{
				float f = (TRANSITION_TICK_COUNT - Mth.lerp(partialTick, this.aimingTicksO, this.aimingTicks)) / TRANSITION_TICK_COUNT;
				return CameraEntityTransparencyCallback.MIN_CAMERA_ENTITY_ALPHA + (1F - CameraEntityTransparencyCallback.MIN_CAMERA_ENTITY_ALPHA) * f;
			}
			
			return 1.0F;
		}
	}
}
