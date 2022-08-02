package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
	@Inject
	(
		method = "renderLevel",
		at = @At("TAIL")
	)
	private void renderLevel(PoseStack poseStack, float partialTick, long nanos, boolean shouldRenderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().updateDynamcRaytrace(camera, poseStack.last().pose(), matrix4f, partialTick);
	}
}
