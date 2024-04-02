package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public abstract class MixinMovementInputFromOptions extends MovementInput
{
	@Inject
	(
		at = @At("TAIL"),
		method = "tick"
	)
	public void tick(boolean isMovingSlowly, CallbackInfo ci)
	{
		Vec2f impulse = ShoulderInstance.getInstance().impulse(this.leftImpulse, this.forwardImpulse);
		this.leftImpulse = impulse.getX();
		this.forwardImpulse = impulse.getY();
	}
}
