package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
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
		event.registerBelow(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "pre_crosshair"), (guiGraphics, deltaTracker) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.preRenderCrosshair(guiGraphics);
			}
		});
		event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "post_crosshair"), (guiGraphics, deltaTracker) ->
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(guiGraphics);
		});
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(event.getCamera(), event.getModelViewMatrix(), event.getProjectionMatrix(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
		}
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfingImpl.getInstance().updatePlayerRotations();
	}
}
