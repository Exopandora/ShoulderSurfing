package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.KeyHandler;

import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MixinMouseHandler
{
	@Inject
	(
		method = "onPress",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
			shift = Shift.BEFORE,
			ordinal = 0
		)
	)
	private void onPress(long window, int button, int action, int modifiers, CallbackInfo info)
	{
		KeyHandler.onInput();
	}
}
