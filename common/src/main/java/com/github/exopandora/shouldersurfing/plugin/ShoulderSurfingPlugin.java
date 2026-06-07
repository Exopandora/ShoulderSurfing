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
import com.github.exopandora.shouldersurfing.plugin.callbacks.PlayerStateCallback;
import com.github.exopandora.shouldersurfing.plugin.callbacks.TargetCameraOffsetCallback;
import org.apache.commons.lang3.StringUtils;

import java.util.ServiceLoader;

public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin {
	@Override
	public void register(IShoulderSurfingRegistrar registrar) {
		registrar.registerAdaptiveItemCallback(new AdaptiveItemCallback());
		registrar.registerCameraCouplingCallback(new CameraCouplingCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallback());
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallbackWhenAiming());
		registrar.registerPlayerStateCallback(new PlayerStateCallback());
		registrar.registerTargetCameraOffsetCallback(50, new TargetCameraOffsetCallback.CameraDistanceAttribute());
		registrar.registerTargetCameraOffsetCallback(100, new TargetCameraOffsetCallback.CameraDistanceAttributePassenger());
		registrar.registerTargetCameraOffsetCallback(150, new TargetCameraOffsetCallback.PassengerModifiersAndMultipliers());
		registrar.registerTargetCameraOffsetCallback(200, new TargetCameraOffsetCallback.SprintingModifiersAndMultipliers());
		registrar.registerTargetCameraOffsetCallback(250, new TargetCameraOffsetCallback.AimingModifiersAndMultipliers());
		registrar.registerTargetCameraOffsetCallback(300, new TargetCameraOffsetCallback.FallFlyingModifiersAndMultipliers());
		registrar.registerTargetCameraOffsetCallback(350, new TargetCameraOffsetCallback.ClimbingModifiersAndMultipliers());
		registrar.registerTargetCameraOffsetCallback(400, new TargetCameraOffsetCallback.CenterWhenLookingDown());
		registrar.registerTargetCameraOffsetCallback(450, new TargetCameraOffsetCallback.DynamicOffsets());
		registrar.registerTargetCameraOffsetCallback(500, new TargetCameraOffsetCallback.OffsetLimits());
		registrar.registerTargetCameraOffsetCallback(550, new TargetCameraOffsetCallback.EntityScale());
		registerCompatibilityCallback(Mods.CREATE, () -> registrar.registerTargetCameraOffsetCallback(2000, new CreateModTargetCameraOffsetCallback()));
		registerCompatibilityCallback(Mods.CURIOS, () -> ServiceLoader.load(ICuriosAdaptiveItemCallback.class).findFirst().ifPresent(registrar::registerAdaptiveItemCallback));
		registerCompatibilityCallback(Mods.COBBLEMON, () -> {
			registrar.registerAdaptiveItemCallback(new CobblemonAdaptiveItemCallback());
			if (CobblemonCompat.supportsRiding()) {
				registrar.registerCameraRotationSetupCallback(new CobblemonCameraRotationSetupCallback());
				registrar.registerPlayerInputCallback(new CobblemonPlayerInputCallback());
				registrar.registerPlayerStateCallback(new CobblemonPlayerStateCallback());
			}
		});
	}
	
	private static void registerCompatibilityCallback(Mods mod, Runnable runnable) {
		if (mod.isLoaded()) {
			String modName = StringUtils.capitalize(mod.name().toLowerCase());
			try {
				ShoulderSurfingCommon.LOGGER.info("Registering compatibility callback for {}", modName);
				runnable.run();
			} catch (Throwable t) {
				ShoulderSurfingCommon.LOGGER.error("Failed to load compatibility callback for {}", modName, t);
			}
		}
	}
}
