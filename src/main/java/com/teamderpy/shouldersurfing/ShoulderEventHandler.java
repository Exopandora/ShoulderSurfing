package com.teamderpy.shouldersurfing;

import org.lwjgl.opengl.GL11;

import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.config.Configuration;

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
				ShoulderRenderBin.rayTraceHit.xCoord -= Minecraft.getMinecraft().thePlayer.posX;
				ShoulderRenderBin.rayTraceHit.yCoord -= Minecraft.getMinecraft().thePlayer.posY;
				ShoulderRenderBin.rayTraceHit.zCoord -= Minecraft.getMinecraft().thePlayer.posZ;
			}
		}
	}
	
	@SubscribeEvent
	public void keyPressed(KeyInputEvent event)
	{
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().inGameHasFocus)
		{
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
			{
				if(ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT.isPressed())
				{
					ShoulderCamera.adjustCameraLeft();
				}
				else if(ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT.isPressed())
				{
					ShoulderCamera.adjustCameraRight();
				}
				else if(ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN.isPressed())
				{
					ShoulderCamera.adjustCameraIn();
				}
				else if(ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT.isPressed())
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
				
				ShoulderSurfing.config.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION, "Third person camera rotation").set(ShoulderCamera.SHOULDER_ROTATION);
				ShoulderSurfing.config.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").set(ShoulderCamera.SHOULDER_ZOOM_MOD);
				ShoulderSurfing.config.save();
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equals(ShoulderSurfing.MODID))
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
	public void preRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(ShoulderRenderBin.skipPlayerRender && Minecraft.getMinecraft().inGameHasFocus)
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
		if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
		{
			float tick = event.partialTicks;
			GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
			
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();
			int scale = resolution.getScaleFactor();
			
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 || (!ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED && Minecraft.getMinecraft().gameSettings.thirdPersonView == 3))
			{
				/** Default Crosshair **/
				
				this.lastX = width * scale / 2;
				this.lastY = height * scale / 2;
				
				this.renderCrosshair(gui, resolution);
			}
			else if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
			{
				/** Dynamic Crosshair **/
				
				GL11.glPushMatrix();;
				
				float diffX = (width * scale / 2 - this.lastX) * tick;
				float diffY = (height * scale / 2 - this.lastY) * tick;
				
				if(ShoulderRenderBin.projectedVector != null)
				{
					diffX = (ShoulderRenderBin.projectedVector.x - this.lastX) * tick;
					diffY = (ShoulderRenderBin.projectedVector.y - this.lastY) * tick;
				}
				
				float crosshairWidth = (this.lastX + diffX) / scale - 7;
				float crosshairHeight = (this.lastY + diffY) / scale - 7;
				
				GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
				GL11.glTranslatef(crosshairWidth * scale, crosshairHeight * scale, 0.0F);
				GL11.glScalef(scale, scale, scale);
				GL11.glTranslatef(-width / 2 + 7, -height / 2 + 7, 0.0F);
				
				this.renderCrosshair(gui, resolution);
				
				this.lastX += diffX;
				this.lastY += diffY;
				
				GL11.glPopMatrix();
			}
			
			/** SHORT-CIRCUIT THE RENDER **/
			if(event.isCancelable())
			{
				event.setCanceled(true);
			}
		}
	}
	
	private void renderCrosshair(GuiIngame gui, ScaledResolution resolution)
	{
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();

		GL11.glPushMatrix();;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
//		GL11.glEnable(GL11.GL_ALPHA);
		
		if(ShoulderSettings.ENABLE_CROSSHAIR || Minecraft.getMinecraft().gameSettings.thirdPersonView != 3)
		{
			gui.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
		}
		
//		GL11.glDisable(GL11.GL_BLEND);
//		GL11.glDisable(GL11.GL_ALPHA);
		GL11.glPopMatrix();
	}
}
