package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;

@Mixin(WorldRenderer.class)
public class MixinWorldRendererFabric
{
	@Inject
	(
		method = "renderLevel",
		at = @At("TAIL")
	)
	private void renderLevel(MatrixStack poseStack, float partialTick, long nanos, boolean shouldRenderBlockOutline, ActiveRenderInfo camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().updateDynamicRaytrace(camera, poseStack.last().pose(), matrix4f, partialTick);
	}
}
