package com.teamderpy.shouldersurfing.event;


import org.lwjgl.opengl.GL11;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

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
		if(event.isCancelable() && event.entityPlayer.equals(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().currentScreen == null)
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
		if(event.type.equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(!Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(ShoulderState.isAiming()))
			{
				event.setCanceled(true);
				return;
			}
			
			if(ShoulderState.getProjected() != null)
			{
				final ScaledResolution mainWindow = event.resolution;
				float scale = mainWindow.getScaleFactor() * ShoulderSurfingHelper.getShadersResmul();
				
				Vec2f window = new Vec2f(mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
				Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
				Vec2f projectedOffset = ShoulderState.getProjected().subtract(center).divide(scale);
				Vec2f lastTranslation = ShoulderState.getLastTranslation();
				Vec2f interpolated = projectedOffset.subtract(lastTranslation).scale(Minecraft.getMinecraft().timer.renderPartialTicks);
				
				ShoulderState.setTranslation(ShoulderState.getLastTranslation().add(interpolated));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
			{
				GL11.glPushMatrix();
				GL11.glTranslatef(ShoulderState.getTranslation().getX(), -ShoulderState.getTranslation().getY(), 0F);
				ShoulderState.setLastTranslation(ShoulderState.getTranslation());
			}
			else
			{
				ShoulderState.setLastTranslation(Vec2f.ZERO);
			}
		}
		//Using BOSSHEALTH to pop matrix because when CROSSHAIRS is cancelled it will not fire RenderGameOverlayEvent#Post and cause a stack overflow
		else if(event.type.equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH) && Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
		{
			if(!Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(ShoulderState.isAiming()))
			{
				return;
			}
			
			GL11.glPopMatrix();
		}
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event)
	{
		final EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
		final PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
		
		if(ShoulderState.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			MovingObjectPosition result = ShoulderSurfingHelper.traceFromEyes(renderView, controller, playerReach, Minecraft.getMinecraft().timer.renderPartialTicks);
			Vec3 position = renderView.getPosition(Minecraft.getMinecraft().timer.renderPartialTicks).subtract(result.hitVec);
			
			ShoulderState.setProjected(ShoulderSurfingHelper.project2D(position));
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		Config.CLIENT.sync();
	}
}
