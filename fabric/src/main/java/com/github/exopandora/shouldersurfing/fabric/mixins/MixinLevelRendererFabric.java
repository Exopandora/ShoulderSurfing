package com.github.exopandora.shouldersurfing.fabric.mixins;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

@Mixin(LevelRenderer.class)
public class MixinLevelRendererFabric
{
	@Inject
	(
		method = "renderLevel",
		at = @At("TAIL")
	)
	private void renderLevel(PoseStack poseStack, float partialTick, long nanos, boolean shouldRenderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().updateDynamicRaytrace(camera, poseStack.last().pose(), matrix4f, partialTick);
	}
}
