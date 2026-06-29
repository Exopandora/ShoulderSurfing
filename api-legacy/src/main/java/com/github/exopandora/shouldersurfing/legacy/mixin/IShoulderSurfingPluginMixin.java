package com.github.exopandora.shouldersurfing.legacy.mixin;

import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.legacy.mixinduck.IShoulderSurfingLegacyPlugin;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IShoulderSurfingPlugin.class)
public interface IShoulderSurfingPluginMixin extends IShoulderSurfingLegacyPlugin {
	@Override
	default void register(IShoulderSurfingRegistrar registrar) {
	}
}
