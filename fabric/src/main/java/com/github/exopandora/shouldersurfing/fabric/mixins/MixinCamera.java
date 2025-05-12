package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public abstract class MixinCamera implements CameraDuck
{
	@ModifyArg
	(
		method = "setRotation(FF)V",
		at = @At
		(
			value = "INVOKE",
			target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
			remap = false
		),
		index = 2
	)
	private float rotationYXZ(float zRot)
	{
		return this.shouldersurfing$getZRot() * -Mth.RAD_TO_DEG + zRot;
	}
}
