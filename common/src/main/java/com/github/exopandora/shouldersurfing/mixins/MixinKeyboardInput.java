package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input
{
	@Inject
	(
		at = @At("TAIL"),
		method = "tick"
	)
	public void tick(boolean isMovingSlowly, CallbackInfo ci)
	{
		Vec2f impulse = ShoulderInstance.getInstance().impulse(this.leftImpulse, this.forwardImpulse);
		this.leftImpulse = impulse.x();
		this.forwardImpulse = impulse.y();
	}
}
