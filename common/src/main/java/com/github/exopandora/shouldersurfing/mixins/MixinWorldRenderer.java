package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
	public void postRender(Entity entity, double x, double y, double z, float partialTick, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, CallbackInfo ci)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().postRenderCameraEntity(entity, partialTick);
		}
	}
}
