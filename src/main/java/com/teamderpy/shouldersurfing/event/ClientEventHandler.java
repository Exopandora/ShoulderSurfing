package com.teamderpy.shouldersurfing.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;
import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static Vec2f lastTickTranslation = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	private static boolean switchPerspective;
	
	public static boolean isAiming;
	public static boolean skipRenderPlayer;
	
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(event.phase.equals(Phase.START))
		{
			if(Minecraft.getInstance().player != null)
			{
				if(!ClientEventHandler.isAiming && ClientEventHandler.isHoldingSpecialItem())
				{
					if(Config.CLIENT.getCrosshairType().doSwitchPerspective() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
					{
						Minecraft.getInstance().gameSettings.thirdPersonView = 0;
						ClientEventHandler.switchPerspective = true;
					}
					
					ClientEventHandler.isAiming = true;
				}
				else if(ClientEventHandler.isAiming && !ClientEventHandler.isHoldingSpecialItem())
				{
					if(!Config.CLIENT.getCrosshairType().doSwitchPerspective() && Minecraft.getInstance().gameSettings.thirdPersonView == 0 && ClientEventHandler.switchPerspective)
					{
						Minecraft.getInstance().gameSettings.thirdPersonView = Perspective.SHOULDER_SURFING.getPerspectiveId();
						ClientEventHandler.switchPerspective = false;
					}
					
					ClientEventHandler.isAiming = false;
				}
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
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS) && Minecraft.getInstance().currentScreen == null)
		{
			float scale = Minecraft.getInstance().getMainWindow().calcGuiScale(Minecraft.getInstance().gameSettings.guiScale, Minecraft.getInstance().getForceUnicodeFont()) * ShoulderSurfing.getShadersResMul();
			
			Vec2f window = new Vec2f(Minecraft.getInstance().getMainWindow().getScaledWidth(), Minecraft.getInstance().getMainWindow().getScaledHeight());
			Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
			
			if(RayTracer.getProjectedVector() != null)
			{
				Vec2f projectedOffset = RayTracer.getProjectedVector().subtract(center).divide(scale);
				ClientEventHandler.translation = ClientEventHandler.lastTickTranslation.add(projectedOffset.subtract(ClientEventHandler.lastTickTranslation).scale(event.getPartialTicks()));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				RenderSystem.translatef(ClientEventHandler.translation.getX(), ClientEventHandler.translation.getY(), 0.0F);
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
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS) && Minecraft.getInstance().currentScreen == null)
		{
			if(Config.CLIENT.getCrosshairType().isDynamic() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				RenderSystem.translatef(-ClientEventHandler.translation.getX(), -ClientEventHandler.translation.getY(), 0.0F);
			}
		}
	}
	
	public static boolean isHoldingSpecialItem()
	{
		Item active = Minecraft.getInstance().player.getActiveItemStack().getItem();
		
		if(active.hasCustomProperties())
		{
			if(active.getPropertyGetter(new ResourceLocation("pull")) != null || active.getPropertyGetter(new ResourceLocation("throwing")) != null)
			{
				return true;
			}
		}
		
		for(ItemStack held : Minecraft.getInstance().player.getHeldEquipment())
		{
			if(held.getItem().hasCustomProperties() && held.getItem().getPropertyGetter(new ResourceLocation("charged")) != null)
			{
				return true;
			}
		}
		
		return false;
	}
}
