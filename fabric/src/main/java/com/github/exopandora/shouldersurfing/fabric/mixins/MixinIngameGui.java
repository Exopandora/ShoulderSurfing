package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class MixinIngameGui
{
	@Shadow
	private @Final Minecraft minecraft;
	
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
		if(ShoulderSurfingImpl.getInstance().getCrosshairRenderer().preRenderCrosshair(poseStack, this.minecraft.getWindow()))
		{
			RenderSystem.color4f(0.0F, 0.0F, 0.0F, 0.0F);
		}
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
		if(ShoulderSurfingImpl.getInstance().getCrosshairRenderer().postRenderCrosshair(poseStack))
		{
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
