package com.teamderpy.shouldersurfing;

import org.lwjgl.util.vector.Vector2f;

import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderEventHandler
{
	@SubscribeEvent
	public void renderEvent(RenderTickEvent event)
	{
		ShoulderRenderBin.skipPlayerRender = false;
		RayTracer.traceFromEyes(1.0F);
		
		if(ShoulderRenderBin.rayTraceHit != null)
		{
			if(Minecraft.getMinecraft().player != null)
			{
				ShoulderRenderBin.rayTraceHit = ShoulderRenderBin.rayTraceHit.subtract(new Vec3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ));
			}
		}
	}
	
	@SubscribeEvent
	public void keyPressed(KeyInputEvent event)
	{
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen == null)
		{
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
			{
				if(ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT.isKeyDown())
				{
					ShoulderCamera.adjustCameraLeft();
				}
				else if(ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT.isKeyDown())
				{
					ShoulderCamera.adjustCameraRight();
				}
				else if(ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN.isKeyDown())
				{
					ShoulderCamera.adjustCameraIn();
				}
				else if(ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT.isKeyDown())
				{
					ShoulderCamera.adjustCameraOut();
				}
				else if(ShoulderSettings.KEYBIND_SWAP_SHOULDER.isPressed())
				{
					ShoulderCamera.swapShoulder();
				}
				else
				{
					return;
				}
				
				Configuration config = ShoulderSurfing.getInstance().getConfig();
				config.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION_YAW, "Third person camera rotation").set(ShoulderCamera.SHOULDER_ROTATION_YAW);
				config.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").set(ShoulderCamera.SHOULDER_ZOOM_MOD);
				config.save();
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(ShoulderSurfing.MODID))
		{
			boolean prev = ShoulderSettings.REPLACE_DEFAULT_3PP;
			
			ShoulderSurfing.getInstance().syncConfig();
			
			if(ShoulderSettings.REPLACE_DEFAULT_3PP != prev)
			{
				if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3 && ShoulderSettings.REPLACE_DEFAULT_3PP)
				{
					Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
				}
				else if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1 && !ShoulderSettings.REPLACE_DEFAULT_3PP)
				{
					Minecraft.getMinecraft().gameSettings.thirdPersonView = 3;
				}
			}
		}
	}
	
	/**
	 * Holds the last coordinate drawing position
	 */
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	private static Vector2f delta;
	private static Vector2f translation;
	
	@SubscribeEvent
	public void preRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(ShoulderRenderBin.skipPlayerRender && event.getEntityPlayer().equals(Minecraft.getMinecraft().player) && (event.getRenderer().getRenderManager().playerViewY != 180 || Minecraft.getMinecraft().inGameHasFocus))
		{
			if(event.isCancelable())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preRenderCrosshairs(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();
			float scale = resolution.getScaleFactor() * ShoulderSurfing.getInstance().getShadersResmul();
			
			delta = this.computeDelta(width, height, scale, event.getPartialTicks());
			translation = this.computeTranslation(width, height, scale, delta);
			
			boolean translate = ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId();
			
			if(translate)
			{
				GlStateManager.translate(ShoulderEventHandler.translation.x, ShoulderEventHandler.translation.y, 0.0F);
			}
			else
			{
				lastX = width * scale / 2;
				lastY = height * scale / 2;
			}
			
			if(ShoulderSettings.OVERRIDE_MOD_CROSSHAIRS && Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
			{
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableAlpha();
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
				Minecraft.getMinecraft().ingameGUI.renderAttackIndicator(event.getPartialTicks(), resolution);
				
				if(event.isCancelable())
				{
					event.setCanceled(true);
				}
				
				if(event.isCanceled() && ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
				{
					this.translateBack();
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postRenderCrosshairs(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			if(ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && Minecraft.getMinecraft().gameSettings.thirdPersonView == ShoulderSettings.getShoulderSurfing3ppId())
			{
				this.translateBack();
			}
		}
	}
	
	private void translateBack()
	{
		lastX += delta.x;
		lastY += delta.y;
		
		GlStateManager.translate(-ShoulderEventHandler.translation.x, -ShoulderEventHandler.translation.y, 0.0F);
	}
	
	private Vector2f computeDelta(int width, int height, float scale, float partial)
	{
		float deltaX = (width * scale / 2 - lastX) * partial;
		float deltaY = (height * scale / 2 - lastY) * partial;
		
		if(ShoulderRenderBin.projectedVector != null)
		{
			deltaX = (ShoulderRenderBin.projectedVector.x - lastX) * partial;
			deltaY = (ShoulderRenderBin.projectedVector.y - lastY) * partial;
		}
		
		return new Vector2f(deltaX, deltaY);
	}
	
	private Vector2f computeTranslation(int width, int height, float scale, Vector2f diff)
	{
		float crosshairWidth = (lastX + diff.x) / scale;
		float crosshairHeight = (lastY + diff.y) / scale;
		
		float translationX = -width / 2 + crosshairWidth;
		float translationY = -height / 2 + crosshairHeight;
		
		return new Vector2f(translationX, translationY);
	}
}
