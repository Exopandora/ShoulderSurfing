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
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	private static Vec2f delta = new Vec2f(0.0F, 0.0F);
	private static Vec2f translation = new Vec2f(0.0F, 0.0F);
	
	public static void renderTickEvent(RenderTickEvent event)
	{
		RayTracer rayTracer = RayTracer.getInstance();
		rayTracer.setSkipPlayerRender(false);
		rayTracer.traceFromEyes(1.0F);
		
		if(rayTracer.getRayTraceHit() != null)
		{
			if(Minecraft.getInstance().player != null)
			{
				rayTracer.setRayTraceHit(rayTracer.getRayTraceHit().subtract(Minecraft.getInstance().player.getPositionVector()));
			}
		}
	}
	
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
	
	public static void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			int width = Minecraft.getInstance().mainWindow.getScaledWidth();
			int height = Minecraft.getInstance().mainWindow.getScaledHeight();
			float scale = Minecraft.getInstance().mainWindow.getScaleFactor(Minecraft.getInstance().gameSettings.guiScale) * ShoulderSurfing.getShadersResmul();
			
			delta = computeDelta(width, height, scale, event.getPartialTicks());
			translation = computeTranslation(width, height, scale);
			
			boolean translate = Config.CLIENT.dynamicCrosshair() && Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId();
			
			if(translate)
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
		
		if(RayTracer.getInstance().getProjectedVector() != null)
		{
			deltaX = (RayTracer.getInstance().getProjectedVector().x - lastX) * partial;
			deltaY = (RayTracer.getInstance().getProjectedVector().y - lastY) * partial;
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
