package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
class EntityRenderDispatcherMixin {
	@Inject(
		method = "submit",
		at = @At("HEAD"),
		cancellable = true
	)
	private <S extends EntityRenderState> void preRender(
		S renderState,
		CameraRenderState camera,
		double x,
		double y,
		double z,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		CallbackInfo ci
	) {
		var cameraEntityRenderer = ShoulderSurfing.getInstance().getCameraEntityRenderer();
		if (renderState == cameraEntityRenderer.getCameraEntityRenderState()) {
			var minecraft = Minecraft.getInstance();
			var entity = minecraft.getCameraEntity();
			var tickRateManager = minecraft.level.tickRateManager();
			var deltaTracker = minecraft.getDeltaTracker();
			var partialTick = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
			if (cameraEntityRenderer.preRenderCameraEntity(entity, partialTick)) {
				ci.cancel();
			}
		}
	}
	
	@Inject(
		method = "submit",
		at = @At("TAIL")
	)
	private <S extends EntityRenderState> void postRender(
		S renderState,
		CameraRenderState camera,
		double x,
		double y,
		double z,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		CallbackInfo ci
	) {
		var cameraEntityRenderer = ShoulderSurfing.getInstance().getCameraEntityRenderer();
		if (renderState == cameraEntityRenderer.getCameraEntityRenderState()) {
			var minecraft = Minecraft.getInstance();
			var entity = minecraft.getCameraEntity();
			var tickRateManager = minecraft.level.tickRateManager();
			var deltaTracker = minecraft.getDeltaTracker();
			var partialTick = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
			cameraEntityRenderer.postRenderCameraEntity(entity, partialTick);
		}
	}
}
