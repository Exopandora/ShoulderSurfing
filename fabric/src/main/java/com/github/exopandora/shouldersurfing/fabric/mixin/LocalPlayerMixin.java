package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after other mods */)
public class LocalPlayerMixin {
	@Shadow
	public Input input;
	
	@Inject(
		method = "aiStep",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
			shift = Shift.AFTER
		)
	)
	private void aiStep(CallbackInfo ci) {
		ShoulderSurfing.getInstance().getInputHandler().updateMovementInput(this.input);
		ShoulderSurfing.getInstance().updatePlayerRotations();
	}
}
