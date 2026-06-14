package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAttackStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAttackStateEventHandler;
import net.minecraft.client.Minecraft;

public class ComputePlayerAttackStateEventHandlerImpl {
	public enum Pre implements ComputePlayerAttackStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerAttackStateEvent event) {
			if (Minecraft.getInstance().options.keyAttack.isDown()) {
				event.setResult(true);
			}
		}
	}
	
	public enum Post implements ComputePlayerAttackStateEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputePlayerAttackStateEvent event) {
			if (event.getCameraEntity().isFallFlying()) {
				event.setResult(false);
			}
		}
	}
}
