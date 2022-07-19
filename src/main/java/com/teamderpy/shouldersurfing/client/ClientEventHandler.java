package com.teamderpy.shouldersurfing.client;

import net.minecraft.client.Minecraft;

public class ClientEventHandler
{
	private final ShoulderInstance shoulderInstance;
	private final ShoulderRenderer shoulderRenderer;
	
	public ClientEventHandler(ShoulderInstance shoulderInstance, ShoulderRenderer shoulderRenderer)
	{
		this.shoulderInstance = shoulderInstance;
		this.shoulderRenderer = shoulderRenderer;
	}
}
