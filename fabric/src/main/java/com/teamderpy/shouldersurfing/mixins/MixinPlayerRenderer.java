package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer
{
	@Inject
	(
		method = "render(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void render(AbstractClientPlayerEntity player, float yRot, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int i, CallbackInfo ci)
	{
		if(player == Minecraft.getInstance().player && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipRenderPlayer())
		{
			ci.cancel();
		}
	}
}
