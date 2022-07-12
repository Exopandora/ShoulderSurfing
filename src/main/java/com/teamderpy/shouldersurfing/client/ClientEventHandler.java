package com.teamderpy.shouldersurfing.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	private final ShoulderInstance shoulderInstance;
	private final ShoulderRenderer shoulderRenderer;
	
	public ClientEventHandler(ShoulderInstance shoulderInstance, ShoulderRenderer shoulderRenderer)
	{
		this.shoulderInstance = shoulderInstance;
		this.shoulderRenderer = shoulderRenderer;
	}
	
	@SubscribeEvent
	public void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase))
		{
			this.shoulderInstance.tick();
		}
	}
	
	@SubscribeEvent
	public void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getEntity().equals(Minecraft.getInstance().player) && Minecraft.getInstance().screen == null && this.shoulderRenderer.skipRenderPlayer())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()))
		{
			this.shoulderRenderer.offsetCrosshair(event.getPoseStack(), event.getWindow(), event.getPartialTick());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGameOverlayEvent#PreLayer and cause a stack overflow
		else if(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id().equals(event.getOverlay().id()))
		{
			this.shoulderRenderer.clearCrosshairOffset(event.getPoseStack());
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("resource")
	public void computeCameraAnglesEvent(ComputeCameraAngles event)
	{
		this.shoulderRenderer.offsetCamera(event.getCamera(), Minecraft.getInstance().level, event.getPartialTick());
	}
	
	@SubscribeEvent
	public void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			this.shoulderRenderer.calcRaytrace(event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
		}
	}
}
