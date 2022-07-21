package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
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
		method = "renderCrosshair",
		at = @At("HEAD")
	)
	private void offsetCrosshair(PoseStack poseStack, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCrosshair(poseStack, this.minecraft.getWindow(), this.minecraft.getFrameTime());
	}
	
	@Inject
	(
		method = "renderCrosshair",
		at = @At("TAIL")
	)
	private void clearCrosshairOffset(PoseStack poseStack, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().clearCrosshairOffset(poseStack);
	}
}
