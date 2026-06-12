package com.github.exopandora.shouldersurfing.client.renderer;

import com.github.exopandora.shouldersurfing.api.client.renderer.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.EventHooks;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.util.Util;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;

public class CameraEntityRenderer implements ICameraEntityRenderer {
	private final ShoulderSurfing instance;
	private float cameraEntityAlpha = 1.0F;
	private boolean isRenderingCameraEntity;
	private EntityRenderState cameraEntityRenderState;
	
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
	
	public int applyCameraEntityAlpha(int color) {
		int cameraEntityAlpha = this.getCameraEntityAlphaAsInt();
		int alpha = ARGB.alpha(color);
		if (cameraEntityAlpha < alpha) {
			return ARGB.transparent(color) + (cameraEntityAlpha << 24);
		}
		return color;
	}
	
	public EntityRenderState getCameraEntityRenderState() {
		return this.cameraEntityRenderState;
	}
	
	public void setCameraEntityRenderState(EntityRenderState cameraEntityRenderState) {
		this.cameraEntityRenderState = cameraEntityRenderState;
	}
	
	public boolean isEntityTransparentPlayer(LivingEntityRenderState state) {
		if (state.isInvisibleToPlayer) {
			return false;
		}
		if (!this.instance.isShoulderSurfing() || !Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			return false;
		}
		if (state != this.cameraEntityRenderState) {
			return false;
		}
		return !Util.isCameraEntityRidingBoat();
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
		return ARGB.as8BitChannel(this.cameraEntityAlpha);
	}
}
