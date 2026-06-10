package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeCameraEntityTransparencyEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeCameraEntityTransparencyEventHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ComputeCameraEntityTransparencyEventHandlerImpl implements ComputeCameraEntityTransparencyEventHandler {
	protected static final float MIN_CAMERA_ENTITY_ALPHA = 0.15F;
	
	@Override
	public void handle(ComputeCameraEntityTransparencyEvent event) {
		IShoulderSurfing instance = ShoulderSurfing.getInstance();
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
}
