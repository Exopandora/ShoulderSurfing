package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;

@Mixin(IngameGui.class)
public class MixinIngameGuiFabric
{
	@Shadow
	protected Minecraft minecraft;
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/IngameGui;renderCrosshair(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
			shift = Shift.BEFORE
		)
	)
	private void offsetCrosshair(MatrixStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCrosshair(poseStack, this.minecraft.getWindow(), this.minecraft.getFrameTime());
	}
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/IngameGui;renderCrosshair(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
			shift = Shift.AFTER
		)
	)
	private void clearCrosshairOffset(MatrixStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().clearCrosshairOffset(poseStack);
	}
}
