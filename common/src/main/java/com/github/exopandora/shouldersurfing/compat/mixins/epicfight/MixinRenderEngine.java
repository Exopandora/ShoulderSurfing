package com.github.exopandora.shouldersurfing.compat.mixins.epicfight;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "yesman.epicfight.client.events.engine.RenderEngine")
public class MixinRenderEngine
{
	@Redirect
	(
		method = "correctCamera",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/ActiveRenderInfo.setRotation(FF)V",
			remap = true
		),
		remap = false
	)
	private void correctCamera(ActiveRenderInfo camera, float yRot, float xRot)
	{
		if(!ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			((ActiveRenderInfoAccessor) camera).invokeSetRotation(yRot, xRot);
		}
		else if(!Config.CLIENT.isCameraDecoupled())
		{
			ShoulderSurfingImpl.getInstance().getCamera().setXRot(xRot);
			ShoulderSurfingImpl.getInstance().getCamera().setYRot(yRot);
		}
	}
	
	@Inject
	(
		method = "correctCamera",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/settings/PointOfView.isMirrored()Z",
			remap = true
		),
		cancellable = true,
		remap = false
	)
	private void correctCamera(CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "setRangedWeaponThirdPerson",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private void setRangedWeaponThirdPerson(CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			ci.cancel();
		}
	}
}
