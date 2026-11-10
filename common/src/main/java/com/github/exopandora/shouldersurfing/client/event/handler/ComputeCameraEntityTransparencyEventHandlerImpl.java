package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.TickEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import com.github.exopandora.shouldersurfing.api.client.event.handler.TickEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public enum ComputeCameraEntityTransparencyEventHandlerImpl implements ComputeCameraEntityTransparencyEventHandler {
	INSTANCE;
	
	private static final float MIN_CAMERA_ENTITY_ALPHA = 0.15F;
	
	@Override
	public void handle(ComputeCameraEntityTransparencyEvent event) {
		var entity = event.getCameraEntity();
		if (!entity.isSpectator()) {
			var instance = IShoulderSurfing.getInstance();
			var renderOffset = instance.getCamera().getRenderOffset();
			var offset = instance.getClientConfig().getCameraConfig().getOffset();
			var alphaX = (float) Mth.clamp(Math.abs(renderOffset.x()) / Math.min(Math.abs(offset.x()), entity.getBbWidth() / 2.0D), 0, 1.0F);
			float alphaY = 0;
			if (renderOffset.y() > 0) {
				alphaY = (float) Mth.clamp(renderOffset.y() / Math.min(offset.y(), entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			} else if (renderOffset.y() < 0) {
				alphaY = (float) Mth.clamp(renderOffset.y() / Math.min(offset.y(), entity.getEyeHeight()), 0, 1.0F);
			}
			var alphaXY = 1.0F;
			if (alphaX < 1.0F || alphaY < 1.0F) {
				alphaXY = (float) Math.sqrt(alphaX * alphaX + alphaY * alphaY);
			}
			var alphaZ = 1.0F - easeOutCirc((float) Mth.clamp(1 - Math.abs(renderOffset.z()) / Math.min(entity.getBbHeight(), offset.z()), 0, 1.0F));
			var result = Mth.clamp(Math.min(alphaXY, alphaZ), MIN_CAMERA_ENTITY_ALPHA, 1.0F);
			if (result < event.getResult()) {
				event.setResult(result);
			}
		}
	}
	
	private static float easeOutCirc(float x) {
		return (float) Math.sqrt(1.0D - Math.pow(x - 1.0D, 2.0D));
	}
	
	public enum Climbing implements ComputeCameraEntityTransparencyEventHandler, TickEventHandler {
		INSTANCE;
		
		private static final int OPAQUE_TICK_COUNT = 5;
		private int opaqueTicks;
		
		@Override
		public void handle(TickEvent event) {
			if (!Config.CLIENT.getPlayerConfig().isPlayerTransparentWhenClimbing()) {
				var connection = Minecraft.getInstance().getConnection();
				//noinspection ConstantValue
				if (connection == null || connection.getLevel() == null) {
					// Guard against (Neo)Forge retrieving data from the server config when calling onClimbable,
					// which might already be unloaded at this point, resulting in a crash.
					return;
				}
				if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && living.onClimbable()) {
					this.opaqueTicks = OPAQUE_TICK_COUNT;
				} else if (this.opaqueTicks > 0) {
					this.opaqueTicks--;
				}
			}
		}
		
		@Override
		public void handle(ComputeCameraEntityTransparencyEvent event) {
			if (!Config.CLIENT.getPlayerConfig().isPlayerTransparentWhenClimbing() && this.opaqueTicks > 0) {
				event.setResult(1.0F);
			}
		}
	}
	
	public enum Aiming implements ComputeCameraEntityTransparencyEventHandler, TickEventHandler {
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
				var f = (TRANSITION_TICK_COUNT - Mth.lerp(event.getPartialTick(), this.aimingTicksO, this.aimingTicks)) / TRANSITION_TICK_COUNT;
				var result = MIN_CAMERA_ENTITY_ALPHA + ((1F - MIN_CAMERA_ENTITY_ALPHA) * f);
				if (result < event.getResult()) {
					event.setResult(result);
				}
			}
		}
	}
}
