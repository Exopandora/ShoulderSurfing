package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraCouplingEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraEntityTransparencyEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraEntityTransparencyEventHandlerImplWhenAiming;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAttackStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerInteractionStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerPickStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerRideBoatStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerUseItemStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputeTargetCameraOffsetEventHandlerImpl;
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
		eventBus.register(new ComputePlayerAimStateEventHandlerImpl());
		eventBus.register(2000, new SetupCameraRotationEventHandlerImpl());
		eventBus.register(new ComputeCameraCouplingEventHandlerImpl());
		eventBus.register(new ComputeCameraEntityTransparencyEventHandlerImpl());
		var cameraEntityTransparencyListener = new ComputeCameraEntityTransparencyEventHandlerImplWhenAiming();
		eventBus.register((TickEventHandler) cameraEntityTransparencyListener);
		eventBus.register((ComputeCameraEntityTransparencyEventHandler) cameraEntityTransparencyListener);
		eventBus.register(0, new ComputePlayerAttackStateEventHandlerImpl.Pre());
		eventBus.register(2000, new ComputePlayerAttackStateEventHandlerImpl.Post());
		eventBus.register(0, new ComputePlayerInteractionStateEventHandlerImpl.Pre());
		eventBus.register(2000, new ComputePlayerInteractionStateEventHandlerImpl.Post());
		eventBus.register(0, new ComputePlayerPickStateEventHandlerImpl.Pre());
		eventBus.register(2000, new ComputePlayerPickStateEventHandlerImpl.Post());
		eventBus.register(new ComputePlayerRideBoatStateEventHandlerImpl());
		eventBus.register(0, new ComputePlayerUseItemStateEventHandlerImpl.Pre());
		eventBus.register(2000, new ComputePlayerUseItemStateEventHandlerImpl.Post());
		eventBus.register(50, new ComputeTargetCameraOffsetEventHandlerImpl.CameraDistanceAttribute());
		eventBus.register(100, new ComputeTargetCameraOffsetEventHandlerImpl.CameraDistanceAttributePassenger());
		eventBus.register(150, new ComputeTargetCameraOffsetEventHandlerImpl.PassengerModifiersAndMultipliers());
		eventBus.register(200, new ComputeTargetCameraOffsetEventHandlerImpl.SprintingModifiersAndMultipliers());
		eventBus.register(250, new ComputeTargetCameraOffsetEventHandlerImpl.AimingModifiersAndMultipliers());
		eventBus.register(300, new ComputeTargetCameraOffsetEventHandlerImpl.FallFlyingModifiersAndMultipliers());
		eventBus.register(350, new ComputeTargetCameraOffsetEventHandlerImpl.ClimbingModifiersAndMultipliers());
		eventBus.register(400, new ComputeTargetCameraOffsetEventHandlerImpl.CenterWhenLookingDown());
		eventBus.register(450, new ComputeTargetCameraOffsetEventHandlerImpl.DynamicOffsets());
		eventBus.register(500, new ComputeTargetCameraOffsetEventHandlerImpl.EntityScale());
		eventBus.register(2000, new ComputeTargetCameraOffsetEventHandlerImpl.OffsetLimits());
		registerCompatibilityEventHandlers(Mods.CREATE, () ->
			eventBus.register(2000, new CreateModEventHandler())
		);
		registerCompatibilityEventHandlers(Mods.CURIOS, () ->
			ServiceLoader.load(ICuriosEventHandler.class).findFirst().ifPresent(eventBus::register)
		);
		registerCompatibilityEventHandlers(Mods.COBBLEMON, () -> {
			CobblemonEventHandler cobblemonEventHandler = new CobblemonEventHandler();
			eventBus.register(cobblemonEventHandler::computePlayerAimState);
			if (CobblemonEventHandler.isRidingSupported()) {
				eventBus.register(500, cobblemonEventHandler::preSetupCameraRotation);
				eventBus.register(1500, cobblemonEventHandler::postSetupCameraRotation);
				eventBus.register(cobblemonEventHandler::forceVanillaPlayerInput);
				eventBus.register(cobblemonEventHandler::computePlayerRideBoatState);
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
