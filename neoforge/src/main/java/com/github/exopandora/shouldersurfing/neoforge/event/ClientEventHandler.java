package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
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

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Pre event)
	{
		if(!Minecraft.getInstance().isPaused())
		{
			ShoulderSurfingImpl.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiLayerEvent.Pre event)
	{
		if(VanillaGuiLayers.CROSSHAIR.equals(event.getName()) && !ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void registerGuiOverlaysEvent(RegisterGuiLayersEvent event)
	{
		event.registerBelow(VanillaGuiLayers.CROSSHAIR, Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pre_crosshair"), (guiGraphics, deltaTracker) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.preRenderCrosshair(guiGraphics);
			}
		});
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "post_crosshair"), (guiGraphics, deltaTracker) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.postRenderCrosshair(guiGraphics);
			}
		});
	}
	
	@SubscribeEvent
	public static void frameGraphSetupEvent(FrameGraphSetupEvent event)
	{
		float partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		ShoulderSurfingImpl.getInstance().getCamera().renderTick(camera.entity(), partialTick);
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(camera, event.getCameraState().viewRotationMatrix, event.getCameraState().projectionMatrix, partialTick);
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfingImpl.getInstance().updatePlayerRotations();
	}
}
