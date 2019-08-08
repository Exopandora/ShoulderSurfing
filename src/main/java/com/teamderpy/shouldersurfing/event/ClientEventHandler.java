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
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	private static Vec2f partial = Vec2f.ZERO;
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
		if(RayTracer.getInstance().skipPlayerRender() && event.getPlayer().equals(Minecraft.getInstance().player) && (event.getRenderer().getRenderManager().playerViewY != 180 || Minecraft.getInstance().isGameFocused()))
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
			
			float partialX = (width * scale / 2 - ClientEventHandler.lastX) * event.getPartialTicks();
			float partialY = (height * scale / 2 - ClientEventHandler.lastY) * event.getPartialTicks();
			
			RayTracer rayTracer = RayTracer.getInstance();
			
			if(rayTracer.getProjectedVector() != null)
			{
				partialX = (rayTracer.getProjectedVector().x - ClientEventHandler.lastX) * event.getPartialTicks();
				partialY = (rayTracer.getProjectedVector().y - ClientEventHandler.lastY) * event.getPartialTicks();
			}
			
			ClientEventHandler.partial = new Vec2f(partialX, partialY);
			
			float crosshairWidth = (ClientEventHandler.lastX + ClientEventHandler.partial.x) / scale;
			float crosshairHeight = (ClientEventHandler.lastY + ClientEventHandler.partial.y) / scale;
			
			float translationX = -width / 2 + crosshairWidth;
			float translationY = -height / 2 + crosshairHeight;
			
			ClientEventHandler.translation = new Vec2f(translationX, translationY);
			
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
				ClientEventHandler.lastX += ClientEventHandler.partial.x;
				ClientEventHandler.lastY += ClientEventHandler.partial.y;
				GlStateManager.translatef(-ClientEventHandler.translation.x, -ClientEventHandler.translation.y, 0.0F);
			}
		}
	}
}
