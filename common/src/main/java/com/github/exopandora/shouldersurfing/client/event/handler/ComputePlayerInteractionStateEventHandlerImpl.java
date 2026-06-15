package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerInteractionStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerInteractionStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

public class ComputePlayerInteractionStateEventHandlerImpl {
	public enum Pre implements ComputePlayerInteractionStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerInteractionStateEvent event) {
			if (Minecraft.getInstance().options.keyUse.isDown() && !event.getCameraEntity().isUsingItem()) {
				HitResult hitResult = Minecraft.getInstance().hitResult;
				if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
					event.setResult(true);
				}
			}
		}
	}
	
	public enum Post implements ComputePlayerInteractionStateEventHandler {
		INSTANCE;
		
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
