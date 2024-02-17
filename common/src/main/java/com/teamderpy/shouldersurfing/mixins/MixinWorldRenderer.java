package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
	@Inject
	(
		method = "renderEntity",
		at = @At("HEAD"),
		cancellable = true
	)
	public void preRender(Entity entity, double x, double y, double z, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity() && ShoulderRenderer.getInstance().preRenderCameraEntity(entity, partialTick))
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "renderEntity",
		at = @At("TAIL")
	)
	public void postRender(Entity entity, double x, double y, double z, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderRenderer.getInstance().postRenderCameraEntity(entity, partialTick, multiBufferSource);
		}
	}
}
