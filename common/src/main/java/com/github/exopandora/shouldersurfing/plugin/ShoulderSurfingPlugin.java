package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.compat.CobblemonCompat;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.plugin.CobblemonAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.compat.plugin.CobblemonCameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.compat.plugin.CobblemonPlayerInputCallback;
import com.github.exopandora.shouldersurfing.compat.plugin.CobblemonPlayerStateCallback;
import com.github.exopandora.shouldersurfing.compat.plugin.CreateModTargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.compat.plugin.ICuriosAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.AdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.CameraCouplingCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.CameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.CameraEntityTransparencyCallbackWhenAiming;
import org.apache.commons.lang3.StringUtils;

import java.util.ServiceLoader;

public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		registrar.registerAdaptiveItemCallback(new AdaptiveItemCallback());
		registrar.registerCameraCouplingCallback(new CameraCouplingCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallbackWhenAiming());
		registerCompatibilityCallback(Mods.CREATE, () -> registrar.registerTargetCameraOffsetCallback(new CreateModTargetCameraOffsetCallback()));
		registerCompatibilityCallback(Mods.CURIOS, () -> ServiceLoader.load(ICuriosAdaptiveItemCallback.class).findFirst().ifPresent(registrar::registerAdaptiveItemCallback));
		registerCompatibilityCallback(Mods.COBBLEMON, () ->
		{
			registrar.registerAdaptiveItemCallback(new CobblemonAdaptiveItemCallback());
			
			if(CobblemonCompat.supportsRiding())
			{
				registrar.registerCameraRotationSetupCallback(new CobblemonCameraRotationSetupCallback());
				registrar.registerPlayerInputCallback(new CobblemonPlayerInputCallback());
				registrar.registerPlayerStateCallback(new CobblemonPlayerStateCallback());
			}
		});
	}
	
	private static void registerCompatibilityCallback(Mods mod, Runnable runnable)
	{
		if(mod.isLoaded())
		{
			String modName = StringUtils.capitalize(mod.name().toLowerCase());
			
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
}
