package com.github.exopandora.shouldersurfing.compat.mixin.optifabricreloaded;

import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// OptiFabric Reforged applies OptiFine, which moves the rotationYXZ call out of setRotation(FF)V into an
// additional setRotation(FFF)V overload, leaving setRotation(FF)V as a plain forwarder. The injector in the
// fabric CameraMixin finds no call site there any more, this class takes over instead. The overload is an
// OptiFine addition, so it is absent on vanilla and it has no obfuscation mapping in any namespace, hence
// remap = false on the selector. Both counters are 0 so that the injector is a no-op instead of an error
// wherever the overload does not exist. This class is only loaded when OptiFabric Reforged is present, see
// ShoulderSurfingCompatMixinPluginFabric#addOptiFabricReforgedMixins.
@Mixin(Camera.class)
class CameraMixin {
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
		return ((CameraDuck) this).shouldersurfing$getZRot() * -Mth.DEG_TO_RAD + zRot;
	}
}
