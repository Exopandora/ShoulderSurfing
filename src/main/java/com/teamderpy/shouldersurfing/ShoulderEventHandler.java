package com.teamderpy.shouldersurfing;

import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.renderer.ShoulderRenderBin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
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
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 3)
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
	public void preRenderPlayer(RenderPlayerEvent.Pre event)
	{
		if(ShoulderRenderBin.skipPlayerRender && (event.getRenderer().getRenderManager().playerViewY != 180 || Minecraft.getMinecraft().inGameHasFocus))
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
			GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
			
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();
			int scale = resolution.getScaleFactor();
			
			if(Minecraft.getMinecraft().gameSettings.showDebugInfo && !Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().player.hasReducedDebug() && !Minecraft.getMinecraft().gameSettings.reducedDebugInfo)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (width / 2), (float) (height / 2), 300);
				Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * scale, -1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * scale, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(-1.0F, -1.0F, -1.0F);
				OpenGlHelper.renderDirections(10);
				GlStateManager.popMatrix();
			}
			else
			{
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
					
					GlStateManager.pushMatrix();
					
					float diffX = (width * scale / 2 - this.lastX) * tick;
					float diffY = (height * scale / 2 - this.lastY) * tick;
					
					if(ShoulderRenderBin.projectedVector != null)
					{
						diffX = (ShoulderRenderBin.projectedVector.x - this.lastX) * tick;
						diffY = (ShoulderRenderBin.projectedVector.y - this.lastY) * tick;
					}
					
					float crosshairWidth = (this.lastX + diffX) / scale - 7;
					float crosshairHeight = (this.lastY + diffY) / scale - 7;
					
					GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
					GlStateManager.translate(crosshairWidth * scale, crosshairHeight * scale, 0.0F);
					GlStateManager.scale(scale, scale, scale);
					GlStateManager.translate(-width / 2 + 7, -height / 2 + 7, 0.0F);
					
					this.renderCrosshair(gui, resolution);
					
					this.lastX += diffX;
					this.lastY += diffY;
					
					GlStateManager.popMatrix();
				}
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
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableAlpha();
		
		if(ShoulderSettings.ENABLE_CROSSHAIR || Minecraft.getMinecraft().gameSettings.thirdPersonView != 3)
		{
			gui.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
		}
		
		if(Minecraft.getMinecraft().gameSettings.attackIndicator == 1 && ShoulderSettings.ENABLE_ATTACK_INDICATOR)
		{
			float cooledAttackStrength = Minecraft.getMinecraft().player.getCooledAttackStrength(0.0F);
			boolean flag = false;

			if(Minecraft.getMinecraft().pointedEntity != null && Minecraft.getMinecraft().pointedEntity instanceof EntityLivingBase && cooledAttackStrength >= 1.0F)
			{
				flag = Minecraft.getMinecraft().player.getCooldownPeriod() > 5.0F;
				flag = flag & ((EntityLivingBase)Minecraft.getMinecraft().pointedEntity).isEntityAlive();
			}

			int y = height / 2 - 7 + 16;
			int x = width / 2 - 8;
			
			if(flag)
			{
				gui.drawTexturedModalRect(x, y, 68, 94, 16, 16);
			}
			else if(cooledAttackStrength < 1.0F)
			{
				int offset = (int)(cooledAttackStrength * 17.0F);
				gui.drawTexturedModalRect(x, y, 36, 94, 16, 4);
				gui.drawTexturedModalRect(x, y, 52, 94, offset, 4);
			}
		}
		
		GlStateManager.disableBlend();
	}
}
