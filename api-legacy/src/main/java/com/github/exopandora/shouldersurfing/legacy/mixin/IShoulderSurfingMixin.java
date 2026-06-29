package com.github.exopandora.shouldersurfing.legacy.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.legacy.mixinduck.IShoulderSurfingLegacy;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IShoulderSurfing.class)
public interface IShoulderSurfingMixin extends IShoulderSurfingLegacy {
}
