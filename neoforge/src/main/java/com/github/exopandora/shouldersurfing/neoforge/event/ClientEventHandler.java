package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ClientEventHandler {
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event) {
		if (Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused()) {
			ShoulderSurfing.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiLayerEvent.Pre event) {
		if (VanillaGuiLayers.CROSSHAIR.equals(event.getName())) {
			if (!IShoulderSurfing.getInstance().getCrosshairRenderer().isCrosshairVisible()) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void registerGuiOverlaysEvent(RegisterGuiLayersEvent event) {
		event.registerBelow(
			VanillaGuiLayers.CROSSHAIR,
			ResourceLocation.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pre_crosshair"),
			(guiGraphics, deltaTracker) -> {
				var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.preRenderCrosshair(guiGraphics);
				}
			}
		);
		event.registerAbove(
			VanillaGuiLayers.CROSSHAIR,
			ResourceLocation.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "post_crosshair"),
			(guiGraphics, deltaTracker) -> {
				var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.postRenderCrosshair(guiGraphics);
				}
			}
		);
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event) {
		if (RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage())) {
			var partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
			var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			var instance = ShoulderSurfing.getInstance();
			var modelViewMatrix = event.getModelViewMatrix();
			var projectionMatrix = event.getProjectionMatrix();
			instance.getCamera().renderTick(camera.getEntity(), partialTick);
			instance.getCrosshairRenderer().renderTick(camera, modelViewMatrix, projectionMatrix, partialTick);
		}
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event) {
		ShoulderSurfing.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfing.getInstance().updatePlayerRotations();
	}
}
