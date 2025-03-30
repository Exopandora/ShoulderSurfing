package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase) && Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused())
		{
			ShoulderSurfingImpl.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()) && !ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void registerGuiOverlaysEvent(RegisterGuiOverlaysEvent event)
	{
		event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "pre_crosshair", (gui, guiGraphics, partialTick, screenWith, screenHeight) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.preRenderCrosshair(guiGraphics);
			}
		});
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "post_crosshair", (gui, guiGraphics, partialTick, screenWith, screenHeight) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.postRenderCrosshair(guiGraphics);
			}
		});
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(event.getCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
		}
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfingImpl.getInstance().updatePlayerRotations();
	}
}
