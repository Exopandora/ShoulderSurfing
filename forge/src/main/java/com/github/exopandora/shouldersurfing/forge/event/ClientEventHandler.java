package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ClientEventHandler {
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event) {
		if (!Minecraft.getInstance().isPaused()) {
			ShoulderSurfing.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event) {
		ShoulderSurfing.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfing.getInstance().updatePlayerRotations();
	}
	
	@SubscribeEvent
	public static void computeCameraAnglesEvent(ViewportEvent.ComputeCameraAngles event) {
		event.setRoll(event.getRoll() - ((CameraDuck) event.getCamera()).shouldersurfing$getZRot());
	}
}
