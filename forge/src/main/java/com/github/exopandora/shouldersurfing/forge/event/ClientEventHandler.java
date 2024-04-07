package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase) && Minecraft.getInstance().level != null)
		{
			ShoulderInstance.getInstance().tick();
			ShoulderRenderer.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			ShoulderRenderer.getInstance().offsetCrosshair(event.getMatrixStack(), event.getWindow(), event.getPartialTicks());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(event.getType().equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH))
		{
			ShoulderRenderer.getInstance().clearCrosshairOffset(event.getMatrixStack());
		}
	}
	
	@SubscribeEvent
	public static void computeCameraAnglesEvent(CameraSetup event)
	{
		ShoulderRenderer renderer = ShoulderRenderer.getInstance();
		renderer.offsetCamera(event.getInfo(), Minecraft.getInstance().level, (float) event.getRenderPartialTicks());
		event.setPitch(event.getInfo().getXRot());
		event.setYaw(event.getInfo().getYRot());
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderWorldLastEvent event)
	{
		ShoulderRenderer.getInstance().updateDynamicRaytrace(Minecraft.getInstance().gameRenderer.getMainCamera(), event.getMatrixStack().last().pose(), event.getProjectionMatrix(), event.getPartialTicks());
	}
	
	@SubscribeEvent
	public static void keyInputEvent(InputEvent event)
	{
		if(Minecraft.getInstance().screen == null)
		{
			KeyHandler.tick();
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
}
