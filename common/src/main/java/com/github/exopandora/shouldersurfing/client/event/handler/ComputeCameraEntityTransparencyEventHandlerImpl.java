package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public enum ComputeCameraEntityTransparencyEventHandlerImpl implements ComputeCameraEntityTransparencyEventHandler {
	INSTANCE;
	
	private static final float MIN_CAMERA_ENTITY_ALPHA = 0.15F;
	
	@Override
	public void handle(ComputeCameraEntityTransparencyEvent event) {
		IShoulderSurfing instance = IShoulderSurfing.getInstance();
		Entity entity = event.getCameraEntity();
		if (isCameraEntityTransparent(instance, entity)) {
			Vec3 renderOffset = instance.getCamera().getRenderOffset();
			float xAlpha = (float) Mth.clamp(Math.abs(renderOffset.x()) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			if (renderOffset.y() > 0) {
				yAlpha = (float) Mth.clamp(renderOffset.y() / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			} else if (renderOffset.y() < 0) {
				yAlpha = (float) Mth.clamp(-renderOffset.y() / -entity.getEyeHeight(), 0, 1.0F);
			}
			float result = Mth.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), MIN_CAMERA_ENTITY_ALPHA, 1.0F);
			if (result < event.getResult()) {
				event.setResult(result);
			}
		}
	}
	
	private static boolean isCameraEntityTransparent(IShoulderSurfing instance, Entity entity) {
		if (entity.isSpectator()) {
			return false;
		}
		Vec3 renderOffset = instance.getCamera().getRenderOffset();
		if (Math.abs(renderOffset.x()) >= (entity.getBbWidth() / 2.0D)) {
			return false;
		}
		return (renderOffset.y() >= 0 && renderOffset.y() < entity.getBbHeight() - entity.getEyeHeight()) ||
			(renderOffset.y() <= 0 && -renderOffset.y() < entity.getEyeHeight());
	}
	
	public enum WhenAiming implements ComputeCameraEntityTransparencyEventHandler, TickEventHandler {
		INSTANCE;
		
		private static final int TRANSITION_TICK_COUNT = 5;
		private int aimingTicks;
		private int aimingTicksO;
		
		@Override
		public void handle(TickEvent event) {
			if (Config.CLIENT.getPlayerConfig().isPlayerTransparentWhenAiming()) {
				this.aimingTicksO = this.aimingTicks;
				if (IShoulderSurfing.getInstance().isAiming()) {
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
}
