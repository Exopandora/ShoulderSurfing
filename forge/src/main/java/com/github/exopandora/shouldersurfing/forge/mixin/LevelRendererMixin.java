package com.github.exopandora.shouldersurfing.forge.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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
		final GraphicsResourceAllocator resourceAllocator,
		final boolean renderOutline,
		final CameraRenderState cameraState,
		final com.mojang.renderpearl.api.buffers.GpuBufferSlice terrainFog,
		final Vector4f fogColor,
		final boolean shouldRenderSky,
		final boolean consistentDepthRequired,
		CallbackInfo ci
	) {
		var partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
		var camera = Minecraft.getInstance().gameRenderer.mainCamera();
		var instance = ShoulderSurfing.getInstance();
		instance.renderTick(camera, cameraState.viewRotationMatrix, cameraState.projectionMatrix, partialTick);
	}
}
