package com.github.exopandora.shouldersurfing.mixins;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IngameGui.class)
public interface IngameGuiAccessor
{
	@Invoker
	boolean invokeCanRenderCrosshairForSpectator(RayTraceResult hitResult);
}
