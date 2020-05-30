package com.teamderpy.shouldersurfing.event;


import com.mojang.blaze3d.systems.RenderSystem;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.asm.InjectionDelegation;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;
import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.math.VectorConverter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler
{
	private static Vec2f lastTranslation = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	private static boolean switchPerspective;
	
	public static boolean isAiming;
	
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
		if(event.getPlayer().equals(Minecraft.getInstance().player) && InjectionDelegation.cameraDistance < 0.80 && Config.CLIENT.keepCameraOutOfHead() && Minecraft.getInstance().currentScreen == null && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
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
				ClientEventHandler.translation = ClientEventHandler.lastTranslation.add(projectedOffset.subtract(ClientEventHandler.lastTranslation).scale(event.getPartialTicks()));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
			{
				RenderSystem.translatef(ClientEventHandler.translation.getX(), ClientEventHandler.translation.getY(), 0.0F);
				ClientEventHandler.lastTranslation = ClientEventHandler.translation;
			}
			else
			{
				ClientEventHandler.lastTranslation = Vec2f.ZERO;
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
	
	@SubscribeEvent
	public static void cameraSetup(CameraSetup event)
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
		{
			final ActiveRenderInfo info = event.getInfo();
			double x = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosX, info.getRenderViewEntity().getPosX());
			double y = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosY, info.getRenderViewEntity().getPosY()) + MathHelper.lerp(event.getRenderPartialTicks(), info.previousHeight, info.height);
			double z = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosZ, info.getRenderViewEntity().getPosZ());
			
			info.setPosition(x, y, z);
			
			InjectionDelegation.cameraDistance = ClientEventHandler.calcCameraDistance(info, info.calcCameraDistance(4.0D * InjectionDelegation.getShoulderZoomMod()));
			
			float yaw = (float) Math.toRadians(InjectionDelegation.getShoulderRotationYaw());
			double dx = MathHelper.cos(yaw) * InjectionDelegation.cameraDistance;
			double dz = MathHelper.sin(yaw) * InjectionDelegation.cameraDistance;
			
			info.movePosition(-dx, 0, dz);
		}
	}
	
	@SubscribeEvent
	public static void renderWorldLast(RenderWorldLastEvent event)
	{
		if(RayTracer.getRayTraceHit() != null)
		{
			RayTracer.setProjectedVector(VectorConverter.project2D(RayTracer.getRayTraceHit(), event.getMatrixStack(), event.getProjectionMatrix()));
			RayTracer.setRayTraceHit(null);
		}
	}
	
	private static double calcCameraDistance(ActiveRenderInfo info, double distance)
	{
		float yaw = (float) Math.toRadians(info.getYaw());
		double yawXZlength = MathHelper.sin((float) Math.toRadians(Config.CLIENT.getShoulderRotationYaw())) * distance;
		
		Vec3d offsetYaw = new Vec3d(MathHelper.cos(yaw) * yawXZlength, 0, MathHelper.sin(yaw) * yawXZlength);
		Vec3d view = info.getProjectedView();
		
		for(int i = 0; i < 8; i++)
		{
			Vec3d offset = offsetYaw.add(((i & 1) * 2 - 1) * 0.1D, ((i >> 1 & 1) * 2 - 1) * 0.1D, ((i >> 2 & 1) * 2 - 1) * 0.1D);
			Vec3d head = view.add(offsetYaw);
			Vec3d camera = view.subtract(info.getViewVector().getX() * distance, info.getViewVector().getY() * distance, info.getViewVector().getZ() * distance).add(offset);
			
			RayTraceContext context = new RayTraceContext(head, camera, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, Minecraft.getInstance().renderViewEntity);
			RayTraceResult result = Minecraft.getInstance().world.rayTraceBlocks(context);
			
			if(result != null)
			{
				double newDistance = result.getHitVec().distanceTo(info.getProjectedView());
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	public static boolean isHoldingSpecialItem()
	{
		if(Minecraft.getInstance().player != null)
		{
			Item item = Minecraft.getInstance().player.getActiveItemStack().getItem();
			
			if(item.hasCustomProperties())
			{
				if(item.getPropertyGetter(new ResourceLocation("pull")) != null || item.getPropertyGetter(new ResourceLocation("throwing")) != null)
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
		}
		
		return false;
	}
}
