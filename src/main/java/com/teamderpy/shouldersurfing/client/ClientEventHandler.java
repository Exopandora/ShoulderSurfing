package com.teamderpy.shouldersurfing.client;

import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	private final ShoulderInstance shoulderInstance = new ShoulderInstance();
	private final ShoulderRenderer shoulderRenderer = new ShoulderRenderer(this.shoulderInstance);
	private final KeyHandler keyHandler = new KeyHandler(this.shoulderInstance);
	
	public ClientEventHandler()
	{
		this.shoulderInstance.changePerspective(Config.CLIENT.getDefaultPerspective());
	}
	
	@SubscribeEvent
	public void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase))
		{
			this.shoulderInstance.tick();
		}
	}
	
	@SubscribeEvent
	public void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getPlayer().equals(Minecraft.getInstance().player) && Minecraft.getInstance().screen == null && this.shoulderRenderer.skipRenderPlayer())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void preRenderGameOverlayEvent(RenderGameOverlayEvent.PreLayer event)
	{
		if(ForgeIngameGui.CROSSHAIR_ELEMENT.equals(event.getOverlay()))
		{
			this.shoulderRenderer.offsetCrosshair(event.getPoseStack(), event.getWindow(), event.getPartialTick());
		}
		//Using BOSS_HEALTH_ELEMENT to pop matrix because when CROSSHAIR_ELEMENT is cancelled it will not fire RenderGameOverlayEvent#PreLayer and cause a stack overflow
		else if(ForgeIngameGui.BOSS_HEALTH_ELEMENT.equals(event.getOverlay()))
		{
			this.shoulderRenderer.clearCrosshairOffset(event.getPoseStack());
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("resource")
	public void cameraSetupEvent(CameraSetup event)
	{
		this.shoulderRenderer.offsetCamera(event.getCamera(), Minecraft.getInstance().level, event.getPartialTick());
	}
	
	@SubscribeEvent
	public void renderLevelLastEvent(RenderLevelLastEvent event)
	{
		this.shoulderRenderer.calcRaytrace(event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
	}
	
	@SubscribeEvent
	public void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getInstance() != null && Minecraft.getInstance().screen == null)
		{
			this.keyHandler.onKeyInput();
		}
	}
}
