package com.teamderpy.shouldersurfing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
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
			if(Minecraft.getMinecraft().thePlayer != null)
			{
				ShoulderRenderBin.rayTraceHit = ShoulderRenderBin.rayTraceHit.subtract(new Vec3d(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ));
			}
		}
	}
	
	@SubscribeEvent
	public void keyPressed(KeyInputEvent event)
	{
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen == null)
		{
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
			{
				if(ShoulderSurfing.KEYBIND_ROTATE_CAMERA_LEFT.isPressed())
				{
					ShoulderCamera.adjustCameraLeft();
				}
				else if(ShoulderSurfing.KEYBIND_ROTATE_CAMERA_RIGHT.isPressed())
				{
					ShoulderCamera.adjustCameraRight();
				}
				else if(ShoulderSurfing.KEYBIND_ZOOM_CAMERA_IN.isPressed())
				{
					ShoulderCamera.adjustCameraIn();
				}
				else if(ShoulderSurfing.KEYBIND_ZOOM_CAMERA_OUT.isPressed())
				{
					ShoulderCamera.adjustCameraOut();
				}
				else
				{
					return;
				}
				
				ShoulderSurfing.config.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION, "Third person camera rotation").set(ShoulderCamera.SHOULDER_ROTATION);
				ShoulderSurfing.config.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").set(ShoulderCamera.SHOULDER_ZOOM_MOD);
				ShoulderSurfing.config.save();
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(ShoulderSurfing.MODID))
		{
			ShoulderSurfing.syncConfig();
		}
	}
	
	/**
	 * Holds the last coordinate drawing position
	 */
	private static float lastX = 0.0F;
	private static float lastY = 0.0F;
	
	@SubscribeEvent
	public void postRenderCrosshairs(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			return;
		}
	}
	
	@SubscribeEvent
	public void preRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(ShoulderRenderBin.skipPlayerRender)
		{
			if(event.isCancelable())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void preRenderCrosshairs(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			float tick = event.getPartialTicks();
			GuiIngame g = Minecraft.getMinecraft().ingameGUI;
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			int l = sr.getScaledWidth();
			int i1 = sr.getScaledHeight();
			
			if(Minecraft.getMinecraft().gameSettings.showDebugInfo && !Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().thePlayer.hasReducedDebug() && !Minecraft.getMinecraft().gameSettings.reducedDebugInfo)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (l / 2), (float) (i1 / 2), 300);
				Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * sr.getScaleFactor(), -1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * sr.getScaleFactor(), 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(-1.0F, -1.0F, -1.0F);
				OpenGlHelper.renderDirections(10);
				GlStateManager.popMatrix();
			}
			else
			{
				if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 || (!ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && Minecraft.getMinecraft().gameSettings.thirdPersonView == 1))
				{
					lastX = sr.getScaledWidth() * sr.getScaleFactor() / 2;;
					lastY = sr.getScaledHeight() * sr.getScaleFactor() / 2;
					
					bind(Gui.ICONS);
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					
					g.drawTexturedModalRect(sr.getScaledWidth() / 2 - 7, sr.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
					
					if(Minecraft.getMinecraft().gameSettings.attackIndicator == 1)
					{
						float f = Minecraft.getMinecraft().thePlayer.getCooledAttackStrength(0.0F);
						
						if(f < 1.0F)
						{
							int i = i1 / 2 - 7 + 16;
							int j = l / 2 - 7;
							int k = (int) (f * 17.0F);
							g.drawTexturedModalRect(j, i, 36, 94, 16, 4);
							g.drawTexturedModalRect(j, i, 52, 94, k, 4);
						}
					}
					
					GlStateManager.disableBlend();
				}
				else if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
				{
					if(ShoulderRenderBin.projectedVector != null)
					{
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						bind(Gui.ICONS);
						
						if(ShoulderRenderBin.rayTraceInReach)
						{
							// GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR,
							// GL11.GL_ONE_MINUS_SRC_COLOR);
							GL14.glBlendColor(0.2f, 0.2f, 1.0f, 1.0f);
							GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						}
						else
						{
							GL14.glBlendColor(1.0f, 0.2f, 0.2f, 1.0f);
							GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						}
						
						float diffX = (ShoulderRenderBin.projectedVector.x - lastX) * tick;
						float diffY = (ShoulderRenderBin.projectedVector.y - lastY) * tick;
						
						int crosshairWidth = (int) ((lastX + diffX) / sr.getScaleFactor() - 7);
						int crosshairHeight = (int) ((lastY + diffY) / sr.getScaleFactor() - 7);
						
						g.drawTexturedModalRect(crosshairWidth, crosshairHeight, 0, 0, 16, 16);
						
						if(Minecraft.getMinecraft().gameSettings.attackIndicator == 1)
						{
							float f = Minecraft.getMinecraft().thePlayer.getCooledAttackStrength(0.0F);
							
							if(f < 1.0F)
							{
								int k = (int) (f * 17.0F);
								g.drawTexturedModalRect(crosshairWidth, crosshairHeight + 16, 36, 94, 16, 4);
								g.drawTexturedModalRect(crosshairWidth, crosshairHeight + 16, 52, 94, k, 4);
							}
						}
						
						lastX = lastX + diffX;
						lastY = lastY + diffY;
						
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
					else if(ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT)
					{
						bind(Gui.ICONS);
						GlStateManager.enableBlend();
						GL14.glBlendColor(1.0f, 0.2f, 0.2f, 1.0f);
						GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						
						float diffX = (sr.getScaledWidth() * sr.getScaleFactor() / 2 - lastX) * tick;
						float diffY = (sr.getScaledHeight() * sr.getScaleFactor() / 2 - lastY) * tick;
						
						int crosshairWidth = (int) ((lastX + diffX) / sr.getScaleFactor() - 7);
						int crosshairHeight = (int) ((lastY + diffY) / sr.getScaleFactor() - 7);
						
						g.drawTexturedModalRect(crosshairWidth, crosshairHeight, 0, 0, 16, 16);
						
						if(Minecraft.getMinecraft().gameSettings.attackIndicator == 1)
						{
							float f = Minecraft.getMinecraft().thePlayer.getCooledAttackStrength(0.0F);
							
							if(f < 1.0F)
							{
								int k = (int) (f * 17.0F);
								g.drawTexturedModalRect(crosshairWidth, crosshairHeight + 16, 36, 94, 16, 4);
								g.drawTexturedModalRect(crosshairWidth, crosshairHeight + 16, 52, 94, k, 4);
							}
						}
						
						lastX = lastX + diffX;
						lastY = lastY + diffY;
						
						GlStateManager.disableBlend();
					}
				}
			}
			
			/** SHORT-CIRCUIT THE RENDER */
			if(event.isCancelable())
			{
				event.setCanceled(true);
			}
		}
	}
	
	private void bind(ResourceLocation res)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
	}
}
