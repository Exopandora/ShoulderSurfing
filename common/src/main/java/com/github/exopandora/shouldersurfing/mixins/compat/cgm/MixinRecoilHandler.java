package com.github.exopandora.shouldersurfing.mixins.compat.cgm;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.mrcrayfish.guns.client.handler.RecoilHandler")
public class MixinRecoilHandler
{
	@Unique
	private float xRot;
	
	@Inject
	(
		method = "onRenderTick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/Minecraft.getDeltaFrameTime()F",
			remap = true
		),
		remap = false
	)
	private void preOnRenderTick(CallbackInfo ci)
	{
		this.xRot = Minecraft.getInstance().player.xRot;
	}
	
	@Inject
	(
		method = "onRenderTick",
		at = @At("TAIL"),
		remap = false
	)
	private void postOnRenderTick(CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			ClientPlayerEntity player = Minecraft.getInstance().player;
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			float delta = player.xRot - this.xRot;
			camera.setXRot(camera.getXRot() + delta);
			player.xRot = this.xRot;
		}
	}
}
