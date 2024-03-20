package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.math.Vec2f;

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
	public void tick(boolean isMovingSlowly, float sneakingSpeedBonus, CallbackInfo ci)
	{
		Vec2f impulse = ShoulderInstance.getInstance().impulse(this.leftImpulse, this.forwardImpulse);
		this.leftImpulse = impulse.x();
		this.forwardImpulse = impulse.y();
	}
}
