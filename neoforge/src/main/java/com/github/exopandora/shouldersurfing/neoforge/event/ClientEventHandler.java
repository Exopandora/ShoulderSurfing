package com.github.exopandora.shouldersurfing.neoforge.event;

import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent event)
	{
		if(TickEvent.Phase.START.equals(event.phase) && Minecraft.getInstance().level != null)
		{
			if(Minecraft.getInstance().screen == null)
			{
				KeyHandler.tick();
			}
			
			ShoulderInstance.getInstance().tick();
			ShoulderRenderer.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()))
		{
			ShoulderRenderer.getInstance().offsetCrosshair(event.getGuiGraphics().pose(), event.getWindow(), event.getPartialTick());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id().equals(event.getOverlay().id()))
		{
			ShoulderRenderer.getInstance().clearCrosshairOffset(event.getGuiGraphics().pose());
		}
	}
	
	@SubscribeEvent
	public static void computeCameraAnglesEvent(ViewportEvent.ComputeCameraAngles event)
	{
		ShoulderRenderer renderer = ShoulderRenderer.getInstance();
		renderer.offsetCamera(event.getCamera(), Minecraft.getInstance().level, (float) event.getPartialTick());
		event.setPitch(event.getCamera().getXRot());
		event.setYaw(event.getCamera().getYRot());
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			ShoulderRenderer.getInstance().updateDynamicRaytrace(event.getCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
		}
	}
	
	@SubscribeEvent
	public static void onDatapackSyncEvent(OnDatapackSyncEvent event)
	{
		if(event.getPlayer() != null)
		{
			ShoulderRenderer.getInstance().resetState(event.getPlayer());
		}
	}
	
	@SubscribeEvent
	public static void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
	{
		ShoulderRenderer.getInstance().resetState(event.getEntity());
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderInstance.getInstance().onMovementInputUpdate(event.getInput());
	}
}
