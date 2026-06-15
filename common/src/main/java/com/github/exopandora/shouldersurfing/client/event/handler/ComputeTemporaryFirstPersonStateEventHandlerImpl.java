package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;

public enum ComputeTemporaryFirstPersonStateEventHandlerImpl implements ComputeTemporaryFirstPersonStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputeTemporaryFirstPersonStateEvent event) {
		if (!event.getResult()) {
			boolean result = switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
				case CrosshairType.STATIC_WITH_1PP, CrosshairType.DYNAMIC_WITH_1PP -> IShoulderSurfing.getInstance().isAiming();
				default -> false;
			};
			event.setResult(result);
		}
	}
}
