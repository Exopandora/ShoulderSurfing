package com.github.exopandora.shouldersurfing.compat.mixin.cgm;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.mrcrayfish.guns.client.handler.RecoilHandler")
public class RecoilHandlerMixin {
	@Redirect(
		method = "onRenderTick",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/player/LocalPlayer.getXRot()F",
			remap = true
		),
		remap = false
	)
	private float getXRot(LocalPlayer player) {
		ShoulderSurfing instance = ShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing()) {
			return instance.getCamera().getXRot();
		}
		return player.getXRot();
	}
	
	@Redirect(
		method = "onRenderTick",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/player/LocalPlayer.setXRot(F)V",
			remap = true
		),
		remap = false
	)
	private void setXRot(LocalPlayer player, float xRot) {
		ShoulderSurfing instance = ShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing()) {
			instance.getCamera().setXRot(xRot);
		} else {
			player.setXRot(xRot);
		}
	}
}
