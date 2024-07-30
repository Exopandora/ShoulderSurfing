package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
	public static void preRenderGuiOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(ShoulderSurfingImpl.getInstance().getCrosshairRenderer().preRenderCrosshair(event.getMatrixStack(), event.getWindow()))
			{
				event.setCanceled(true);
			}
		}
		// Using BOSSHEALTH to pop matrix because when CROSSHAIRS is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(event.getType().equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(event.getMatrixStack());
		}
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderWorldLastEvent event)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(Minecraft.getInstance().gameRenderer.getMainCamera(), event.getMatrixStack().last().pose(), event.getProjectionMatrix(), event.getPartialTicks());
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(InputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getMovementInput());
		ShoulderSurfingImpl.getInstance().updatePlayerRotations();
	}
}
