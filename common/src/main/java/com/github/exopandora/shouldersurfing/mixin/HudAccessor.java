package com.github.exopandora.shouldersurfing.mixin;

import net.minecraft.client.gui.Hud;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Hud.class)
public interface HudAccessor {
	@Invoker
	boolean invokeCanRenderCrosshairForSpectator(HitResult hitResult);
}
