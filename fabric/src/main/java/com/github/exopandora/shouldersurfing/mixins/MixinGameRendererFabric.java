package com.github.exopandora.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRendererFabric
{
	@Shadow
	private Camera mainCamera;
	
	@Inject
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
			shift = Shift.AFTER
		)
	)
	private void onCameraSetup(float partialTick, long nanos, PoseStack poseStack, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCamera(this.mainCamera, Minecraft.getInstance().level, partialTick);
	}
}
