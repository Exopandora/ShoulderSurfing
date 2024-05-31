package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("removal")
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
	public static void preRenderGuiOverlayEvent(RenderGameOverlayEvent.PreLayer event)
	{
		if(ForgeIngameGui.CROSSHAIR_ELEMENT.equals(event.getOverlay()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().offsetCrosshair(event.getMatrixStack(), event.getWindow(), event.getPartialTicks());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(ForgeIngameGui.BOSS_HEALTH_ELEMENT.equals(event.getOverlay()))
		{
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().clearCrosshairOffset(event.getMatrixStack());
		}
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelLastEvent event)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(Minecraft.getInstance().gameRenderer.getMainCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getInput());
	}
}
