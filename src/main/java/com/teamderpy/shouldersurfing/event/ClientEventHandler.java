package com.teamderpy.shouldersurfing.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.CrosshairType;
import com.teamderpy.shouldersurfing.math.RayTracer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	private static Vec2f delta = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	private static int itemUseTicks;
	
	@SubscribeEvent
	public static void renderTickEvent(RenderTickEvent event)
	{
		RayTracer rayTracer = RayTracer.getInstance();
		rayTracer.setSkipPlayerRender(false);
		rayTracer.traceFromEyes(1.0F);
		
		if(rayTracer.getRayTraceHit() != null && Minecraft.getInstance().player != null)
		{
			rayTracer.setRayTraceHit(rayTracer.getRayTraceHit().subtract(Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView()));
		}
	}
	
	@SubscribeEvent
	public static void livingEntityUseItemEventTick(LivingEntityUseItemEvent.Tick event)
	{
		if(Config.CLIENT.getCrosshairType().equals(CrosshairType.STATIC_WITH_1PP) && ClientEventHandler.itemUseTicks < 2 && event.getEntity().equals(Minecraft.getInstance().player))
		{
			if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = 0;
				ClientEventHandler.itemUseTicks = 2;
			}
		}
	}
	
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(Config.CLIENT.getCrosshairType().equals(CrosshairType.STATIC_WITH_1PP) && event.phase.equals(Phase.START))
		{
			if(ClientEventHandler.itemUseTicks > 0)
			{
				ClientEventHandler.itemUseTicks--;
			}
			
			if(ClientEventHandler.itemUseTicks == 1)
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = Config.CLIENT.getShoulderSurfing3ppId();
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(RayTracer.getInstance().skipPlayerRender() && event.getEntityPlayer().equals(Minecraft.getInstance().player) && (event.getRenderer().getRenderManager().playerViewY != 180 || Minecraft.getInstance().isGameFocused()))
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
			int width = Minecraft.getInstance().mainWindow.getScaledWidth();
			int height = Minecraft.getInstance().mainWindow.getScaledHeight();
			float scale = Minecraft.getInstance().mainWindow.calcGuiScale(Minecraft.getInstance().gameSettings.guiScale, Minecraft.getInstance().getForceUnicodeFont()) * ShoulderSurfing.getShadersResMul();
			
			ClientEventHandler.delta = computeDelta(width, height, scale, event.getPartialTicks());
			ClientEventHandler.translation = computeTranslation(width, height, scale);
			
			if(Config.CLIENT.getCrosshairType().isDynamic(Minecraft.getInstance().player.getActiveItemStack()) && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				GlStateManager.translatef(ClientEventHandler.translation.x, ClientEventHandler.translation.y, 0.0F);
			}
			else
			{
				ClientEventHandler.lastX = width * scale / 2;
				ClientEventHandler.lastY = height * scale / 2;
			}
		}
	}
	
	@SubscribeEvent
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(Config.CLIENT.getCrosshairType().isDynamic(Minecraft.getInstance().player.getActiveItemStack()) && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				translateBack();
			}
		}
	}
	
	private static void translateBack()
	{
		ClientEventHandler.lastX += ClientEventHandler.delta.x;
		ClientEventHandler.lastY += ClientEventHandler.delta.y;
		GlStateManager.translatef(-ClientEventHandler.translation.x, -ClientEventHandler.translation.y, 0.0F);
	}
	
	private static Vec2f computeDelta(int width, int height, float scale, float partial)
	{
		float deltaX = (width * scale / 2 - ClientEventHandler.lastX) * partial;
		float deltaY = (height * scale / 2 - ClientEventHandler.lastY) * partial;
		
		RayTracer rayTracer = RayTracer.getInstance();
		
		if(rayTracer.getProjectedVector() != null)
		{
			deltaX = (rayTracer.getProjectedVector().x - ClientEventHandler.lastX) * partial;
			deltaY = (rayTracer.getProjectedVector().y - ClientEventHandler.lastY) * partial;
		}
		
		return new Vec2f(deltaX, deltaY);
	}
	
	private static Vec2f computeTranslation(int width, int height, float scale)
	{
		float crosshairWidth = (ClientEventHandler.lastX + delta.x) / scale;
		float crosshairHeight = (ClientEventHandler.lastY + delta.y) / scale;
		
		float translationX = -width / 2 + crosshairWidth;
		float translationY = -height / 2 + crosshairHeight;
		
		return new Vec2f(translationX, translationY);
	}
}
