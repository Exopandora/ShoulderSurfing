package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.client.event.AddFramePassEvent;
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
	
	@SubscribeEvent
	public static void replaceCrosshairRenderer(AddGuiOverlayLayersEvent event) {
		event.getLayeredDraw().replace(ForgeLayeredDraw.PRE_SLEEP_STACK, ForgeLayeredDraw.CROSSHAIR, (guiGraphicsExtractor, deltaTracker) -> {
			var crosshairRenderer = ShoulderSurfing.getInstance().getCrosshairRenderer();
			if (crosshairRenderer.isCrosshairVisible()) {
				crosshairRenderer.preRenderCrosshair(guiGraphicsExtractor);
				crosshairRenderer.postRenderCrosshair(guiGraphicsExtractor);
			}
		});
	}
}
