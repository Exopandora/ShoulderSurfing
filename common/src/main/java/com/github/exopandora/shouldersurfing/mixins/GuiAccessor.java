package com.github.exopandora.shouldersurfing.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor
{
	@Invoker
	boolean invokeCanRenderCrosshairForSpectator(HitResult hitResult);
}
