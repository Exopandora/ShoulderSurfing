package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(value = LivingEntityRenderer.class)
public class MixinLivingEntityRenderer
{
	@Inject
	(
		method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void render(LivingEntity entity, float yRot, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipEntityRendering())
		{
			ci.cancel();
		}
	}
}
