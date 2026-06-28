package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
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
	
	@SuppressWarnings("DataFlowIssue")
	@SubscribeEvent
	public static void modifyCrosshairPosition(AddGuiOverlayLayersEvent event) {
		// Add below == add before, add above == add after
		final var preSleepDrawLayer = event.getLayeredDraw().getChild(ForgeLayeredDraw.PRE_SLEEP_STACK);
		preSleepDrawLayer.addBelow(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pre_render_crosshair"),
			ForgeLayeredDraw.CROSSHAIR, (guiGraphicsExtractor, _) -> {
			var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
			if (crosshairRenderer.isCrosshairVisible()) {
				crosshairRenderer.preRenderCrosshair(guiGraphicsExtractor);
			}
		});
		preSleepDrawLayer.addConditionTo(ForgeLayeredDraw.CROSSHAIR, () -> ShoulderSurfing.getInstance().getCrosshairRenderer().isCrosshairVisible());
		preSleepDrawLayer.addAbove(Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "post_render_crosshair"),
			ForgeLayeredDraw.CROSSHAIR, (guiGraphicsExtractor, _) -> {
			var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
			if (crosshairRenderer.isCrosshairVisible()) {
				crosshairRenderer.postRenderCrosshair(guiGraphicsExtractor);
			}
		});
	}
}
