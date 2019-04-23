package com.teamderpy.shouldersurfing.event;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.RayTracer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	private static Vec2f delta = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	
	@SubscribeEvent
	public static void renderTickEvent(RenderTickEvent event)
	{
		RayTracer rayTracer = RayTracer.getInstance();
		rayTracer.setSkipPlayerRender(false);
		rayTracer.traceFromEyes(1.0F);
		
		if(rayTracer.getRayTraceHit() != null && Minecraft.getInstance().player != null)
		{
			rayTracer.setRayTraceHit(rayTracer.getRayTraceHit().subtract(Minecraft.getInstance().player.getPositionVector()));
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
			float scale = Minecraft.getInstance().mainWindow.getScaleFactor(Minecraft.getInstance().gameSettings.guiScale) * ShoulderSurfing.getShadersResMul();
			
			delta = computeDelta(width, height, scale, event.getPartialTicks());
			translation = computeTranslation(width, height, scale);
			
			if(Config.CLIENT.dynamicCrosshair() && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				GlStateManager.translatef(translation.x, translation.y, 0.0F);
			}
			else
			{
				lastX = width * scale / 2;
				lastY = height * scale / 2;
			}
		}
	}
	
	@SubscribeEvent
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(Config.CLIENT.dynamicCrosshair() && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
			{
				translateBack();
			}
		}
	}
	
	private static void translateBack()
	{
		lastX += delta.x;
		lastY += delta.y;
		
		GlStateManager.translatef(-translation.x, -translation.y, 0.0F);
	}
	
	private static Vec2f computeDelta(int width, int height, float scale, float partial)
	{
		float deltaX = (width * scale / 2 - lastX) * partial;
		float deltaY = (height * scale / 2 - lastY) * partial;
		
		RayTracer rayTracer = RayTracer.getInstance();
		
		if(rayTracer.getProjectedVector() != null)
		{
			deltaX = (rayTracer.getProjectedVector().x - lastX) * partial;
			deltaY = (rayTracer.getProjectedVector().y - lastY) * partial;
		}
		
		return new Vec2f(deltaX, deltaY);
	}
	
	private static Vec2f computeTranslation(int width, int height, float scale)
	{
		float crosshairWidth = (lastX + delta.x) / scale;
		float crosshairHeight = (lastY + delta.y) / scale;
		
		float translationX = -width / 2 + crosshairWidth;
		float translationY = -height / 2 + crosshairHeight;
		
		return new Vec2f(translationX, translationY);
	}
}
