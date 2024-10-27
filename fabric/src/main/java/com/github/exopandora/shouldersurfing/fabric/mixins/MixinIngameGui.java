package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class MixinIngameGui
{
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
	private void preRenderCrosshair(MatrixStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().preRenderCrosshair(poseStack);
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
	private void postRenderCrosshair(MatrixStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(poseStack);
	}
}
