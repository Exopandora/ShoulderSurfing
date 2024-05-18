package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
	@Shadow
	private @Final Camera mainCamera;
	
	@Inject
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
			shift = Shift.AFTER
		)
	)
	private void onCameraSetup(float partialTick, long nanos, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCamera(this.mainCamera, Minecraft.getInstance().level, partialTick);
	}
}
