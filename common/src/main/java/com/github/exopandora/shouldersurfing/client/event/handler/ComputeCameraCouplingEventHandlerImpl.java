package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraCouplingEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraCouplingEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

public enum ComputeCameraCouplingEventHandlerImpl implements ComputeCameraCouplingEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeCameraCouplingEvent event) {
		var player = Minecraft.getInstance().player;
		if (player != null) {
			event.setResult(player.getVehicle() instanceof AbstractMinecart);
		}
	}
}
