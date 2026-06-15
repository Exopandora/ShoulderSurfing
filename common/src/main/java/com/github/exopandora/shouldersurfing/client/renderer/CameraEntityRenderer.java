package com.github.exopandora.shouldersurfing.client.renderer;

import com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.client.EventHooks;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.FastColor.ABGR32;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CameraEntityRenderer implements ICameraEntityRenderer {
	private final ShoulderSurfing instance;
	private float cameraEntityAlpha = 1.0F;
	private boolean isRenderingCameraEntity;
	
	public CameraEntityRenderer(ShoulderSurfing instance) {
		this.instance = instance;
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick) {
		if (this.isCameraEntityRenderingSkipped(entity)) {
			return true;
		}
		if (this.instance.isShoulderSurfing() && Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			this.cameraEntityAlpha = EventHooks.getCameraEntityAlpha(entity, partialTick);
		} else {
			this.cameraEntityAlpha = 1.0F;
		}
		this.isRenderingCameraEntity = true;
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick) {
		this.isRenderingCameraEntity = false;
	}
	
	private boolean isCameraEntityRenderingSkipped(Entity cameraEntity) {
		if (!this.instance.isShoulderSurfing() || cameraEntity.isSpectator()) {
			return false;
		}
		ShoulderSurfingCamera camera = this.instance.getCamera();
		if (camera.isInsideEntity(cameraEntity)) {
			return true;
		} else if (camera.isLookingUp()) {
			return true;
		}
		return EntityHelper.isScoping(cameraEntity);
	}
	
	public int applyCameraEntityAlphaContextAware(int color) {
		return this.isRenderingCameraEntity ? this.applyCameraEntityAlpha(color) : color;
	}
	
	public float applyCameraEntityAlphaContextAware(float alpha) {
		return this.isRenderingCameraEntity ? Math.min(alpha, this.cameraEntityAlpha) : alpha;
	}
	
	public int applyCameraEntityAlpha(int color) {
		int cameraEntityAlpha = this.getCameraEntityAlphaAsInt();
		int alpha = ABGR32.alpha(color);
		if (cameraEntityAlpha < alpha) {
			return ABGR32.transparent(color) + (cameraEntityAlpha << 24);
		}
		return color;
	}
	
	@Override
	public boolean isRenderingCameraEntity() {
		return this.isRenderingCameraEntity;
	}
	
	@Override
	public float getCameraEntityAlpha() {
		return this.cameraEntityAlpha;
	}
	
	@Override
	public int getCameraEntityAlphaAsInt() {
		return Mth.floor(this.cameraEntityAlpha * 255.0F);
	}
}
