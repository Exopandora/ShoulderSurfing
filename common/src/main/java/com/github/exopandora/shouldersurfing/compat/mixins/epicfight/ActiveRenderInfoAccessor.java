package com.github.exopandora.shouldersurfing.compat.mixins.epicfight;

import net.minecraft.client.renderer.ActiveRenderInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoAccessor
{
	@Invoker
	void invokeSetRotation(float yRot, float xRot);
}
