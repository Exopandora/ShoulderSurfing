package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerUseItemStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerUseItemStateEventHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;

public class ComputePlayerUseItemStateEventHandlerImpl {
	public static class Pre implements ComputePlayerUseItemStateEventHandler {
		@Override
		public void handle(ComputePlayerUseItemStateEvent event) {
			if (event.getCameraEntity().isUsingItem() && !event.getCameraEntity().getUseItem().has(DataComponents.FOOD)) {
				event.setResult(true);
			} else if (event.getCameraEntity() instanceof Player player && player.isScoping()) {
				event.setResult(true);
			}
		}
	}
	
	public static class Post implements ComputePlayerUseItemStateEventHandler {
		@Override
		public void handle(ComputePlayerUseItemStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			}
		}
	}
}
