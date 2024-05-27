package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
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
		if(Phase.START.equals(event.phase) && Minecraft.getInstance().level != null)
		{
			ShoulderSurfingImpl.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().offsetCrosshair(event.getGuiGraphics().pose(), event.getWindow(), event.getPartialTick());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id().equals(event.getOverlay().id()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().clearCrosshairOffset(event.getGuiGraphics().pose());
		}
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
	}
}
