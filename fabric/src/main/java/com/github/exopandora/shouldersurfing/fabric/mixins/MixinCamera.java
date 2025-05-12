package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera implements CameraDuck
{
	@Shadow
	private @Final Quaternion rotation;
	
	@Inject
	(
		method = "setRotation(FF)V",
		at = @At
		(
			value = "INVOKE",
			target = "Lcom/mojang/math/Quaternion;mul(Lcom/mojang/math/Quaternion;)V",
			shift = Shift.AFTER
		)
	)
	private void setRotation(CallbackInfo ci)
	{
		this.rotation.mul(Vector3f.ZP.rotationDegrees(this.shouldersurfing$getZRot()));
	}
}
