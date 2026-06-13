package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTemporaryFirstPersonStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTemporaryFirstPersonStateEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.PerspectiveConfig;

public class ComputeTemporaryFirstPersonStateEventHandlerImpl {
	public enum WhenAiming implements ComputeTemporaryFirstPersonStateEventHandler {
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
	
	public enum ConstrainedSpace implements ComputeTemporaryFirstPersonStateEventHandler, TickEventHandler {
		INSTANCE;
		
		private boolean isSpaceConstrained;
		private int cooldown;
		
		@Override
		public void handle(ComputeTemporaryFirstPersonStateEvent event) {
			if (!event.getResult()) {
				event.setResult(this.isSpaceConstrained || this.cooldown > 0);
			}
		}
		
		@Override
		public void handle(TickEvent event) {
			PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			if (perspectiveConfig.isTemporaryFirstPersonInConstrainedSpacesEnabled()) {
				IShoulderSurfingCamera camera = IShoulderSurfing.getInstance().getCamera();
				this.isSpaceConstrained = isSpaceConstrained(camera);
				if (this.isSpaceConstrained) {
					this.cooldown = perspectiveConfig.getTemporaryFirstPersonInConstrainedSpacesCooldown();
				} else if (this.cooldown > 0) {
					this.cooldown--;
				}
			}
		}
		
		private static boolean isSpaceConstrained(IShoulderSurfingCamera camera) {
			PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
			if (Math.abs(camera.getRenderOffset().x) < perspectiveConfig.getTemporaryFirstPersonOffsetXThreshold()) {
				return true;
			} else if (Math.abs(camera.getRenderOffset().y) < perspectiveConfig.getTemporaryFirstPersonOffsetYThreshold()) {
				return true;
			}
			return Math.abs(camera.getRenderOffset().z) < perspectiveConfig.getTemporaryFirstPersonOffsetZThreshold();
		}
	}
}
