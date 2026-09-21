package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
class LevelRendererMixin {
	@Inject(
		method = "render",
		at = @At("TAIL")
	)
	private void render(
		GraphicsResourceAllocator resourceAllocator,
		DeltaTracker deltaTracker,
		boolean renderOutline,
		CameraRenderState cameraState,
		Matrix4fc modelViewMatrix,
		GpuBufferSlice terrainFog,
		Vector4f fogColor,
		boolean shouldRenderSky,
		CallbackInfo ci
	) {
		var partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
		var camera = Minecraft.getInstance().gameRenderer.mainCamera();
		var instance = ShoulderSurfing.getInstance();
		instance.renderTick(camera, modelViewMatrix, cameraState.projectionMatrix, partialTick);
	}
}
