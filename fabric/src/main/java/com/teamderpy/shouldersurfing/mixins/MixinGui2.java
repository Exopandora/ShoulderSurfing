package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class MixinGui2
{
	@Shadow
	protected Minecraft minecraft;
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
			shift = Shift.BEFORE
		)
	)
	private void offsetCrosshair(PoseStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCrosshair(poseStack, this.minecraft.getWindow(), this.minecraft.getFrameTime());
	}
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
			shift = Shift.AFTER
		)
	)
	private void clearCrosshairOffset(PoseStack poseStack, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().clearCrosshairOffset(poseStack);
	}
}
