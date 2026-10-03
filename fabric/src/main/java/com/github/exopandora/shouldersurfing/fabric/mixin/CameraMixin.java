package com.github.exopandora.shouldersurfing.fabric.mixin;

import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
abstract class CameraMixin implements CameraDuck {
	// OptiFabric Reforged applies OptiFine, which moves the rotationYXZ call out of setRotation(FF)V into an
	// additional setRotation(FFF)V overload. setRotation(FF)V still exists on that setup, it is just a plain
	// forwarder, so this injector still runs but finds no call site, which defaultRequire: 1 would turn into a
	// hard failure. require = 0 is what keeps the injection optional there. It cannot move to the compat mixin,
	// because it is a property of this injection point and vanilla has to keep working without OptiFabric; the
	// OptiFine counterpart that applies the roll on that setup lives in
	// compat/mixin/optifabricreloaded/CameraMixin.
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
}
