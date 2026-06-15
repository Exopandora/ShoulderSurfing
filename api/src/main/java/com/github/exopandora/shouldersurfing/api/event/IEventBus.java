package com.github.exopandora.shouldersurfing.api.event;

import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraCouplingEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAttackStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerPickStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerRideBoatStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerUseItemStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ForceVanillaPlayerInputEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.SetupCameraRotationEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;

public interface IEventBus {
	int DEFAULT_PRIORITY = 1000;
	
	void register(int priority, ComputePlayerAimStateEventHandler handler);
	
	default void register(ComputePlayerAimStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputeCameraCouplingEventHandler handler);
	
	default void register(ComputeCameraCouplingEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputeCameraEntityTransparencyEventHandler handler);
	
	default void register(ComputeCameraEntityTransparencyEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputePlayerAttackStateEventHandler handler);
	
	default void register(ComputePlayerAttackStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputePlayerInteractionStateEventHandler handler);
	
	default void register(ComputePlayerInteractionStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputePlayerPickStateEventHandler handler);
	
	default void register(ComputePlayerPickStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputePlayerRideBoatStateEventHandler handler);
	
	default void register(ComputePlayerRideBoatStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputePlayerUseItemStateEventHandler handler);
	
	default void register(ComputePlayerUseItemStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputeTargetCameraOffsetEventHandler handler);
	
	default void register(ComputeTargetCameraOffsetEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ForceVanillaPlayerInputEventHandler handler);
	
	default void register(ForceVanillaPlayerInputEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, SetupCameraRotationEventHandler handler);
	
	default void register(SetupCameraRotationEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, ComputeTemporaryFirstPersonStateEventHandler handler);
	
	default void register(ComputeTemporaryFirstPersonStateEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	void register(int priority, TickEventHandler handler);
	
	default void register(TickEventHandler handler) {
		this.register(DEFAULT_PRIORITY, handler);
	}
	
	<T extends Event> T fire(T event);
}
