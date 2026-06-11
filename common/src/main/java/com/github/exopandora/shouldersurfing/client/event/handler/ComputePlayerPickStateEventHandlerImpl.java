package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerPickStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerPickStateEventHandler;
import net.minecraft.client.Minecraft;

public class ComputePlayerPickStateEventHandlerImpl {
	public enum Pre implements ComputePlayerPickStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerPickStateEvent event) {
			if (Minecraft.getInstance().options.keyPickItem.isDown()) {
				event.setResult(true);
			}
		}
	}
	
	public enum Post implements ComputePlayerPickStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerPickStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			}
		}
	}
}
