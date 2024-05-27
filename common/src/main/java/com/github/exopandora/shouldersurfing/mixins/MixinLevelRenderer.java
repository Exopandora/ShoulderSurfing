package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer
{
	@Inject
	(
		method = "renderEntity",
		at = @At("HEAD"),
		cancellable = true
	)
	public void preRender(Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().preRenderCameraEntity(entity, partialTick))
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "renderEntity",
		at = @At("TAIL")
	)
	public void postRender(Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().postRenderCameraEntity(entity, partialTick);
		}
	}
}
