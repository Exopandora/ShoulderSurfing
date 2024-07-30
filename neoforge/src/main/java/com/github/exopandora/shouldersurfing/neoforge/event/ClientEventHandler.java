package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
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
		if(VanillaGuiLayers.CROSSHAIR.equals(event.getName()))
		{
			if(ShoulderSurfingImpl.getInstance().getCrosshairRenderer().offsetCrosshair(event.getGuiGraphics().pose(), Minecraft.getInstance().getWindow()))
			{
				event.setCanceled(true);
			}
		}
		// Using HOTBAR to pop matrix, because when CROSSHAIR is cancelled, it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(VanillaGuiLayers.HOTBAR.equals(event.getName()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().clearCrosshairOffset(event.getGuiGraphics().pose());
		}
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
