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
public class MixinLivingEntityRenderer<T extends LivingEntity>
{
	@Inject
	(
		method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	public void preRender(T entity, float entityYRot, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().preRenderCameraEntity(entity, partialTick))
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At("TAIL")
	)
	public void postRender(T entity, float entityYRot, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderRenderer.getInstance().postRenderCameraEntity(entity, partialTick, multiBufferSource);
		}
	}
}
