package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerInteractionStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;

public class ComputePlayerInteractionStateEventHandlerImpl {
	public static class Pre implements ComputePlayerInteractionStateEventHandler {
		@Override
		public void handle(ComputePlayerInteractionStateEvent event) {
			if (Minecraft.getInstance().options.keyUse.isDown() && !event.getCameraEntity().isUsingItem()) {
				event.setResult(true);
			}
		}
	}
	
	public static class Post implements ComputePlayerInteractionStateEventHandler {
		@Override
		public void handle(ComputePlayerInteractionStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			} else if (Config.CLIENT.getObjectPickerConfig().getPickVector() == PickVector.PLAYER && Config.CLIENT.getCrosshairConfig().getCrosshairType() == CrosshairType.DYNAMIC) {
				event.setResult(false);
			}
		}
	}
}
