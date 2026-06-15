package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerRideBoatStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerRideBoatStateEventHandler;
import net.minecraft.world.entity.vehicle.Boat;

public enum ComputePlayerRideBoatStateEventHandlerImpl implements ComputePlayerRideBoatStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputePlayerRideBoatStateEvent event) {
		if (event.getVehicle() instanceof Boat) {
			event.setResult(true);
		}
	}
}
