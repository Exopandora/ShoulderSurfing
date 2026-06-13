package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.FrameGraphSetupEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Matrix4f;

public class ClientEventHandler {
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event) {
		if (!Minecraft.getInstance().isPaused()) {
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
			Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pre_crosshair"),
			(guiGraphics, deltaTracker) -> {
				CrosshairRenderer crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.preRenderCrosshair(guiGraphics);
				}
			}
		);
		event.registerAbove(
			VanillaGuiLayers.CROSSHAIR,
			Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "post_crosshair"),
			(guiGraphics, deltaTracker) -> {
				CrosshairRenderer crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
				if (crosshairRenderer.isCrosshairVisible()) {
					crosshairRenderer.postRenderCrosshair(guiGraphics);
				}
			}
		);
	}
	
	@SubscribeEvent
	public static void frameGraphSetupEvent(FrameGraphSetupEvent event) {
		float partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		ShoulderSurfing instance = ShoulderSurfing.getInstance();
		Matrix4f modelViewMatrix = event.getCameraState().viewRotationMatrix;
		Matrix4f projectionMatrix = event.getCameraState().projectionMatrix;
		instance.getCamera().renderTick(camera.entity(), partialTick);
		instance.getCrosshairRenderer().renderTick(camera, modelViewMatrix, projectionMatrix, partialTick);
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event) {
		ShoulderSurfing.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfing.getInstance().updatePlayerRotations();
	}
}
