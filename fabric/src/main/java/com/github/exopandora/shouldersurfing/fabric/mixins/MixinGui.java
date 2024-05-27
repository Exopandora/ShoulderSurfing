package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(Gui.class)
public abstract class MixinGui
{
	@Shadow
	private @Final Minecraft minecraft;
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V",
			shift = Shift.BEFORE
		)
	)
	private void offsetCrosshair(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().offsetCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), partialTick);
	}
	
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V",
			shift = Shift.AFTER
		)
	)
	private void clearCrosshairOffset(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCrosshairRenderer().clearCrosshairOffset(guiGraphics.pose());
	}
	
	@Redirect
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current() && this.minecraft.player.isScoping();
	}
}
