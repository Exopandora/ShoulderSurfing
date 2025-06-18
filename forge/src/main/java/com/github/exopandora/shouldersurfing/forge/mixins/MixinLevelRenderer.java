package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
	@Inject
	(
		method = "renderLevel",
		at = @At("TAIL")
	)
	private void renderLevel(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean shouldRenderBlockOutline, Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBufferSlice, Vector4f fogColor, boolean skipFogRendering, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(camera, modelViewMatrix, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(true));
	}
}
