package com.github.exopandora.shouldersurfing.mixins;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public interface ClientInputAccessor
{
	@Accessor
	void setMoveVector(Vec2 moveVector);
}
