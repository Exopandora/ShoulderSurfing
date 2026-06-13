package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraCouplingEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraEntityTransparencyEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAttackStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerInteractionStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerPickStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerRideBoatStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerUseItemStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeTargetCameraOffsetEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeTemporaryFirstPersonStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.SetupCameraRotationEventHandlerImpl;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.cobblemon.event.handler.CobblemonEventHandler;
import com.github.exopandora.shouldersurfing.compat.create.event.handler.CreateModEventHandler;
import com.github.exopandora.shouldersurfing.compat.curios.event.handler.ICuriosEventHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.ServiceLoader;

public class BuiltinPlugin implements IShoulderSurfingPlugin {
	@Override
	public void register(IEventBus eventBus) {
		eventBus.register(ComputePlayerAimStateEventHandlerImpl.INSTANCE);
		eventBus.register(2000, SetupCameraRotationEventHandlerImpl.INSTANCE);
		eventBus.register(ComputeCameraCouplingEventHandlerImpl.INSTANCE);
		eventBus.register(ComputeCameraEntityTransparencyEventHandlerImpl.INSTANCE);
		eventBus.register((TickEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.WhenAiming.INSTANCE);
		eventBus.register((ComputeCameraEntityTransparencyEventHandler) ComputeCameraEntityTransparencyEventHandlerImpl.WhenAiming.INSTANCE);
		eventBus.register(0, ComputePlayerAttackStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerAttackStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(0, ComputePlayerInteractionStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerInteractionStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(0, ComputePlayerPickStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerPickStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(ComputePlayerRideBoatStateEventHandlerImpl.INSTANCE);
		eventBus.register(0, ComputePlayerUseItemStateEventHandlerImpl.Pre.INSTANCE);
		eventBus.register(2000, ComputePlayerUseItemStateEventHandlerImpl.Post.INSTANCE);
		eventBus.register(50, ComputeTargetCameraOffsetEventHandlerImpl.CameraDistanceAttribute.INSTANCE);
		eventBus.register(100, ComputeTargetCameraOffsetEventHandlerImpl.CameraDistanceAttributePassenger.INSTANCE);
		eventBus.register(150, ComputeTargetCameraOffsetEventHandlerImpl.PassengerModifiersAndMultipliers.INSTANCE);
		eventBus.register(200, ComputeTargetCameraOffsetEventHandlerImpl.SprintingModifiersAndMultipliers.INSTANCE);
		eventBus.register(250, ComputeTargetCameraOffsetEventHandlerImpl.AimingModifiersAndMultipliers.INSTANCE);
		eventBus.register(300, ComputeTargetCameraOffsetEventHandlerImpl.FallFlyingModifiersAndMultipliers.INSTANCE);
		eventBus.register(350, ComputeTargetCameraOffsetEventHandlerImpl.ClimbingModifiersAndMultipliers.INSTANCE);
		eventBus.register(400, ComputeTargetCameraOffsetEventHandlerImpl.CenterWhenLookingDown.INSTANCE);
		eventBus.register(450, ComputeTargetCameraOffsetEventHandlerImpl.DynamicOffsets.INSTANCE);
		eventBus.register(500, ComputeTargetCameraOffsetEventHandlerImpl.EntityScale.INSTANCE);
		eventBus.register(2000, ComputeTargetCameraOffsetEventHandlerImpl.OffsetLimits.INSTANCE);
		eventBus.register(ComputeTemporaryFirstPersonStateEventHandlerImpl.WhenAiming.INSTANCE);
		eventBus.register((ComputeTemporaryFirstPersonStateEventHandler) ComputeTemporaryFirstPersonStateEventHandlerImpl.ConstrainedSpace.INSTANCE);
		eventBus.register((TickEventHandler) ComputeTemporaryFirstPersonStateEventHandlerImpl.ConstrainedSpace.INSTANCE);
		registerCompatibilityEventHandlers(Mods.CREATE, () ->
			eventBus.register(2000, CreateModEventHandler.INSTANCE)
		);
		registerCompatibilityEventHandlers(Mods.CURIOS, () ->
			ServiceLoader.load(ICuriosEventHandler.class).findFirst().ifPresent(eventBus::register)
		);
		registerCompatibilityEventHandlers(Mods.COBBLEMON, () -> {
			eventBus.register(CobblemonEventHandler.INSTANCE::computePlayerAimState);
			if (CobblemonEventHandler.isRidingSupported()) {
				eventBus.register(500, CobblemonEventHandler.INSTANCE::preSetupCameraRotation);
				eventBus.register(1500, CobblemonEventHandler.INSTANCE::postSetupCameraRotation);
				eventBus.register(CobblemonEventHandler.INSTANCE::forceVanillaPlayerInput);
				eventBus.register(CobblemonEventHandler.INSTANCE::computePlayerRideBoatState);
			}
		});
	}
	
	private static void registerCompatibilityEventHandlers(Mods mod, Runnable runnable) {
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
