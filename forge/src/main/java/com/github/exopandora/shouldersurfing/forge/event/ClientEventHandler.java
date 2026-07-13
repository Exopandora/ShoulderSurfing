package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event) {
		if (Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused()) {
			ShoulderSurfing.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event) {
		if (RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage())) {
			var partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			var instance = ShoulderSurfing.getInstance();
			instance.getCamera().renderTick(event.getCamera().getEntity(), partialTick);
			var modelViewMatrix = event.getPoseStack();
			var projectionMatrix = RenderSystem.getProjectionMatrix();
			instance.getCrosshairRenderer().renderTick(event.getCamera(), modelViewMatrix, projectionMatrix, partialTick);
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
