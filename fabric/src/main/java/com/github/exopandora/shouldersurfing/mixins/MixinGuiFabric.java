package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Perspective;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGuiFabric
{
	@Shadow
	protected Minecraft minecraft;
	
	@Shadow
	protected float scopeScale;
	
	@Shadow
	abstract void renderSpyglassOverlay(float partialTick);
	
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
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z",
			shift = Shift.BEFORE
		)
	)
	private void renderSpyglass(PoseStack poseStack, float partialTick, CallbackInfo ci)
	{
		if(this.minecraft.player.isScoping() && Perspective.SHOULDER_SURFING.equals(Perspective.current()))
		{
			this.renderSpyglassOverlay(this.scopeScale);
		}
	}
}
