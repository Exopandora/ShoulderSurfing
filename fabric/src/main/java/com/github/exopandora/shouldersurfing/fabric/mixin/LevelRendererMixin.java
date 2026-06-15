package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(
		method = "renderLevel",
		at = @At("TAIL")
	)
	private void renderLevel(
		PoseStack poseStack,
		float partialTick,
		long nanos,
		boolean shouldRenderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightTexture lightTexture,
		Matrix4f projectionMatrix,
		CallbackInfo ci
	) {
		ShoulderSurfing instance = ShoulderSurfing.getInstance();
		instance.getCamera().renderTick(camera.getEntity(), partialTick);
		instance.getCrosshairRenderer().renderTick(camera, poseStack.last().pose(), projectionMatrix, partialTick);
	}
}
