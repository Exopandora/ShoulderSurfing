package com.teamderpy.shouldersurfing.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.CrosshairType;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;
import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static Vec2f lastTickTranslation = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;;
	private static int itemUseTicks;
	
	public static boolean skipRenderPlayer = false;
	
	@SubscribeEvent
	public static void livingEntityUseItemEventTick(LivingEntityUseItemEvent.Tick event)
	{
		if(ClientEventHandler.itemUseTicks < 2 && event.getEntity().equals(Minecraft.getInstance().player))
		{
			ClientEventHandler.itemUseTicks = 2;
			
			if(Config.CLIENT.getCrosshairType().equals(CrosshairType.STATIC_WITH_1PP) && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = Perspective.FIRST_PERSON.getPerspectiveId();
			}
		}
	}
	
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(event.phase.equals(Phase.START))
		{
			if(ClientEventHandler.itemUseTicks > 0)
			{
				ClientEventHandler.itemUseTicks--;
			}
			
			if(Config.CLIENT.getCrosshairType().equals(CrosshairType.STATIC_WITH_1PP) && ClientEventHandler.itemUseTicks == 1)
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = Perspective.SHOULDER_SURFING.getPerspectiveId();
			}
			
			ClientEventHandler.skipRenderPlayer = false;
			
			RayTracer.traceFromEyes(1.0F);
			
			if(RayTracer.getRayTraceHit() != null && Minecraft.getInstance().player != null)
			{
				RayTracer.setRayTraceHit(RayTracer.getRayTraceHit().subtract(Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView()));
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.getPlayer().equals(Minecraft.getInstance().player) && ClientEventHandler.skipRenderPlayer && Config.CLIENT.keepCameraOutOfHead() && Minecraft.getInstance().currentScreen == null)
		{
			if(event.isCancelable())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			float scale = Minecraft.getInstance().mainWindow.calcGuiScale(Minecraft.getInstance().gameSettings.guiScale, Minecraft.getInstance().getForceUnicodeFont()) * ShoulderSurfing.getShadersResMul();
			
			Vec2f window = new Vec2f(Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight());
			Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
			
			if(RayTracer.getProjectedVector() != null)
			{
				Vec2f projectedOffset = RayTracer.getProjectedVector().subtract(center).divide(scale);
				ClientEventHandler.translation = ClientEventHandler.lastTickTranslation.add(projectedOffset.subtract(ClientEventHandler.lastTickTranslation).scale(event.getPartialTicks()));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				GlStateManager.translatef(ClientEventHandler.translation.getX(), ClientEventHandler.translation.getY(), 0.0F);
				ClientEventHandler.lastTickTranslation = ClientEventHandler.translation;
			}
			else
			{
				ClientEventHandler.lastTickTranslation = Vec2f.ZERO;
			}
		}
	}
	
	@SubscribeEvent
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(Config.CLIENT.getCrosshairType().isDynamic() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				GlStateManager.translatef(-ClientEventHandler.translation.getX(), -ClientEventHandler.translation.getY(), 0.0F);
			}
		}
	}
	
	public static boolean isAiming()
	{
		return ClientEventHandler.itemUseTicks > 0;
	}
}
