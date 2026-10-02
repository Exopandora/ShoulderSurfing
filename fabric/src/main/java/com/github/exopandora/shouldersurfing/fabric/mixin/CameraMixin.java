package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
abstract class CameraMixin implements CameraDuck {
	@ModifyArg(
		method = "setRotation(FF)V",
		at = @At(
			value = "INVOKE",
			target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
			remap = false
		),
		index = 2,
		require = 0
	)
	private float rotationYXZ(float zRot) {
		return this.shouldersurfing$getZRot() * -Mth.DEG_TO_RAD + zRot;
	}
	
	// OptiFine moves the rotationYXZ call out of setRotation(FF)V into an additional setRotation(FFF)V
	// overload, which leaves setRotation(FF)V as a plain forwarder and the injection above without a call
	// site. The overload is an OptiFine addition, so it is absent on vanilla and it has no obfuscation
	// mapping in any namespace, hence remap = false on the selector. Both counters are 0 so that the
	// injector is a no-op instead of an error wherever the overload does not exist.
	@ModifyArg(
		method = "setRotation(FFF)V",
		at = @At(
			value = "INVOKE",
			target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
			remap = false
		),
		remap = false,
		index = 2,
		require = 0,
		expect = 0
	)
	private float rotationYXZOptiFine(float zRot) {
		return this.shouldersurfing$getZRot() * -Mth.DEG_TO_RAD + zRot;
	}
}
