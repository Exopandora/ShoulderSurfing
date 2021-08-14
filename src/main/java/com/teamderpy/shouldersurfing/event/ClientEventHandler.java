package com.teamderpy.shouldersurfing.event;


import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderState;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
				ShoulderState.setSwitchPerspective(false);
			}
			
			ShoulderState.setAiming(ShoulderSurfingHelper.isHoldingSpecialItem());
			
			if(ShoulderState.isAiming() && Config.CLIENT.getCrosshairType().doSwitchPerspective() && ShoulderState.doShoulderSurfing())
			{
				ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				ShoulderState.setSwitchPerspective(true);
			}
			else if(!ShoulderState.isAiming() && Perspective.FIRST_PERSON.equals(Perspective.current()) && ShoulderState.doSwitchPerspective())
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
			if(ShoulderState.getCameraDistance() < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderState.doShoulderSurfing())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(ShoulderState.getProjected() != null)
			{
				final ScaledResolution mainWindow = event.getResolution();
				float scale = mainWindow.getScaleFactor() * ShoulderSurfingHelper.getShadersResmul();
				
				Vec2f window = new Vec2f(mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
				Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
				Vec2f projectedOffset = ShoulderState.getProjected().subtract(center).divide(scale);
				Vec2f lastTranslation = ShoulderState.getLastTranslation();
				Vec2f interpolated = projectedOffset.subtract(lastTranslation).scale(event.getPartialTicks());
				
				ShoulderState.setTranslation(ShoulderState.getLastTranslation().add(interpolated));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(ShoulderState.getTranslation().getX(), -ShoulderState.getTranslation().getY(), 0F);
				ShoulderState.setLastTranslation(ShoulderState.getTranslation());
			}
			else
			{
				ShoulderState.setLastTranslation(Vec2f.ZERO);
			}
		}
		//Using BOSSHEALTH to pop matrix because when CROSSHAIRS is cancelled it will not fire RenderGameOverlayEvent#Post and cause a stack overflow
		else if(event.getType().equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH) && Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
		{
			GlStateManager.popMatrix();
		}
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event)
	{
		final Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		final PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
		
		if(ShoulderState.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			RayTraceResult result = ShoulderSurfingHelper.traceFromEyes(renderView, controller, playerReach, event.getPartialTicks());
			Vec3d position = result.hitVec.subtract(renderView.getPositionEyes(event.getPartialTicks()).subtract(0, renderView.getEyeHeight(), 0));
			
			ShoulderState.setProjected(ShoulderSurfingHelper.project2D(position));
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		Config.CLIENT.sync();
	}
}
