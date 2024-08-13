package com.github.exopandora.shouldersurfing.compat.mixins.epicfight;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface AccessorCamera
{
	@Invoker
	void invokeSetRotation(float yRot, float xRot);
}
