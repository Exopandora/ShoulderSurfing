package com.teamderpy.shouldersurfing.proxy;

import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.event.KeyHandler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void setup()
	{
		MinecraftForge.EVENT_BUS.addListener(KeyHandler::keyInputEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::renderTickEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::preRenderPlayerEvent);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::livingEntityUseItemEventTick);
		MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::preRenderGameOverlayEvent);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ClientEventHandler::postRenderGameOverlayEvent);
	}
}
