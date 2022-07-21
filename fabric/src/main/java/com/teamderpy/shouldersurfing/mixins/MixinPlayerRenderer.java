package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer
{
	@Inject
	(
		method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void render(AbstractClientPlayer player, float yRot, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci)
	{
		if(player == Minecraft.getInstance().player && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipRenderPlayer())
		{
			ci.cancel();
		}
	}
}
