package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingRenderer.class)
public class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>>
{
	@Inject
	(
		method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void preRender(T entity, float yRot, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().preRenderCameraEntity(entity, partialTick))
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
		at = @At("TAIL")
	)
	private void postRender(T entity, float yRot, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderRenderer.getInstance().postRenderCameraEntity(entity, partialTick, multiBufferSource);
		}
	}
}
