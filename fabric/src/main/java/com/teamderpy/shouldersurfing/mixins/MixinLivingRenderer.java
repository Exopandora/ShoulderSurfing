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
import net.minecraft.entity.LivingEntity;

@Mixin(value = LivingRenderer.class)
public class MixinLivingRenderer
{
	@Inject
	(
		method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void render(LivingEntity entity, float yRot, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipEntityRendering())
		{
			ci.cancel();
		}
	}
}
