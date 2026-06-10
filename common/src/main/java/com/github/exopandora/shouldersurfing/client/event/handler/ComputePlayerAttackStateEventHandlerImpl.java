package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAttackStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAttackStateEventHandler;
import net.minecraft.client.Minecraft;

public class ComputePlayerAttackStateEventHandlerImpl {
	public static class Pre implements ComputePlayerAttackStateEventHandler {
		@Override
		public void handle(ComputePlayerAttackStateEvent event) {
			if (Minecraft.getInstance().options.keyAttack.isDown()) {
				event.setResult(true);
			}
		}
	}
	
	public static class Post implements ComputePlayerAttackStateEventHandler {
		@Override
		public void handle(ComputePlayerAttackStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			}
		}
	}
}
