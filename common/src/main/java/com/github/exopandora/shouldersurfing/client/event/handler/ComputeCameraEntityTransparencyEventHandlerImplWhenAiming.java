package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;

import static com.github.exopandora.shouldersurfing.client.event.handler.ComputeCameraEntityTransparencyEventHandlerImpl.MIN_CAMERA_ENTITY_ALPHA;

public class ComputeCameraEntityTransparencyEventHandlerImplWhenAiming implements ComputeCameraEntityTransparencyEventHandler, TickEventHandler {
	private static final int TRANSITION_TICK_COUNT = 5;
	private int aimingTicks;
	private int aimingTicksO;
	
	@Override
	public void handle(TickEvent event) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparentWhenAiming()) {
			this.aimingTicksO = this.aimingTicks;
			if (ShoulderSurfingImpl.getInstance().isAiming()) {
				if (this.aimingTicks < TRANSITION_TICK_COUNT) {
					this.aimingTicks++;
				}
			} else if (this.aimingTicks > 0) {
				this.aimingTicks--;
			}
		}
	}
	
	@Override
	public void handle(ComputeCameraEntityTransparencyEvent event) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparentWhenAiming()) {
			float f = (TRANSITION_TICK_COUNT - Mth.lerp(event.getPartialTick(), this.aimingTicksO, this.aimingTicks)) / TRANSITION_TICK_COUNT;
			float result = MIN_CAMERA_ENTITY_ALPHA + ((1F - MIN_CAMERA_ENTITY_ALPHA) * f);
			if (result < event.getResult()) {
				event.setResult(result);
			}
		}
	}
}
