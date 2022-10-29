package com.teamderpy.shouldersurfing.event;

import org.lwjgl.input.Keyboard;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
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
			ShoulderInstance.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.entityPlayer.equals(Minecraft.getMinecraft().thePlayer) && Minecraft.getMinecraft().currentScreen != null && ShoulderRenderer.getInstance().skipRenderPlayer())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		boolean doRender = Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(Minecraft.getMinecraft().objectMouseOver, ShoulderInstance.getInstance().isAiming());
		
		if(event.type.equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(doRender)
			{
				ShoulderRenderer.getInstance().offsetCrosshair(event.resolution, event.partialTicks);
			}
			else
			{
				event.setCanceled(true);
			}
		}
		//Using BOSSHEALTH to pop matrix because when CROSSHAIRS is cancelled it will not fire RenderGameOverlayEvent#Post and cause a stack overflow
		else if(doRender && event.type.equals(RenderGameOverlayEvent.ElementType.BOSSHEALTH))
		{
			ShoulderRenderer.getInstance().clearCrosshairOffset();
		}
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event)
	{
		ShoulderRenderer.getInstance().updateDynamicRaytrace(event.partialTicks);
	}
	
	@SubscribeEvent
	public void keyInputEvent(InputEvent event)
	{
		KeyHandler.onInput();
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		Config.CLIENT.sync();
	}
	
	@SubscribeEvent
	public void onGuiClosed(GuiOpenEvent event)
	{
		if(event.gui == null)
		{
			Keyboard.enableRepeatEvents(true);
		}
	}
}
