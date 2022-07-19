package com.teamderpy.shouldersurfing.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui
{
	@Shadow
	protected Minecraft minecraft;
	@Unique
	private boolean isCrosshairOffset;

	@Redirect
	(
		method = "renderCrosshair",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType)
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(this.minecraft.hitResult, ShoulderInstance.getInstance().isAiming());
	}

	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SourceFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DestFactor;)V"))
	private void offsetCrosshair(PoseStack poseStack, CallbackInfo ci) {
		this.isCrosshairOffset = true;
		ShoulderSurfing.INSTANCE.getShoulderRenderer().offsetCrosshair(poseStack, this.minecraft.getWindow(), this.minecraft.getFrameTime());
	}

	@Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
	private void isCrosshairOffset(PoseStack poseStack, CallbackInfo ci) {
		if (isCrosshairOffset) {
			this.isCrosshairOffset = false;
			ShoulderSurfing.INSTANCE.getShoulderRenderer().clearCrosshairOffset(poseStack);
		}
	}
}
