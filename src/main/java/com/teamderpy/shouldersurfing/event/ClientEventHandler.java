package com.teamderpy.shouldersurfing.event;


import java.util.Optional;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler
{
	@SubscribeEvent
	public void clientTickEvent(ClientTickEvent event)
	{
		if(event.phase.equals(Phase.START))
		{
			if(!Perspective.FIRST_PERSON.equals(Perspective.current()))
			{
				ShoulderSurfing.STATE.setSwitchPerspective(false);
			}
			
			ShoulderSurfing.STATE.setAiming(ShoulderSurfingHelper.isHoldingSpecialItem());
			
			if(ShoulderSurfing.STATE.isAiming() && Config.CLIENT.getCrosshairType().doSwitchPerspective() && ShoulderSurfing.STATE.doShoulderSurfing())
			{
				ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				ShoulderSurfing.STATE.setSwitchPerspective(true);
			}
			else if(!ShoulderSurfing.STATE.isAiming() && Perspective.FIRST_PERSON.equals(Perspective.current()) && ShoulderSurfing.STATE.doSwitchPerspective())
			{
				ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
			}
		}
	}
	
	@SubscribeEvent
	public void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getEntityPlayer().equals(Minecraft.getMinecraft().player) && Minecraft.getMinecraft().currentScreen == null)
		{
			if(ShoulderSurfing.STATE.getCameraDistance() < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderSurfing.STATE.doShoulderSurfing())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(ShoulderSurfing.STATE.getProjected() != null)
			{
				final ScaledResolution mainWindow = event.getResolution();
				float scale = mainWindow.getScaleFactor() * ShoulderSurfing.getShadersResmul();
				
				Vec2f window = new Vec2f(mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
				Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
				Vec2f projectedOffset = ShoulderSurfing.STATE.getProjected().subtract(center).divide(scale);
				Vec2f lastTranslation = ShoulderSurfing.STATE.getLastTranslation();
				Vec2f interpolated = projectedOffset.subtract(lastTranslation).scale(event.getPartialTicks());
				
				ShoulderSurfing.STATE.setTranslation(ShoulderSurfing.STATE.getLastTranslation().add(interpolated));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderSurfing.STATE.doShoulderSurfing())
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(ShoulderSurfing.STATE.getTranslation().getX(), -ShoulderSurfing.STATE.getTranslation().getY(), 0F);
				ShoulderSurfing.STATE.setLastTranslation(ShoulderSurfing.STATE.getTranslation());
			}
			else
			{
				ShoulderSurfing.STATE.setLastTranslation(Vec2f.ZERO);
			}
		}
	}
	
	@SubscribeEvent
	public void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS) && Config.CLIENT.getCrosshairType().isDynamic() && ShoulderSurfing.STATE.doShoulderSurfing())
		{
			GlStateManager.popMatrix();
		}
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event)
	{
		final Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		final PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
		
		if(ShoulderSurfing.STATE.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.showCrosshairFarther() ? ShoulderSurfing.RAYTRACE_DISTANCE : 0;
			Optional<RayTraceResult> result = ShoulderSurfingHelper.traceFromEyes(renderView, controller, playerReach, event.getPartialTicks());
			
			if(result.isPresent())
			{
				Vec3d position = result.get().hitVec.subtract(renderView.getPositionEyes(event.getPartialTicks()).subtract(0, renderView.getEyeHeight(), 0));
				ShoulderSurfing.STATE.setProjected(ShoulderSurfingHelper.project2D(position));
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		Config.CLIENT.sync();
	}
}
