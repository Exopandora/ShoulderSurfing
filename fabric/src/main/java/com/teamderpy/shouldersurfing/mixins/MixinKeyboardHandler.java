package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.KeyHandler;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler
{
	@Shadow
	private Minecraft minecraft;
	
	@Inject
	(
		method = "keyPress",
		at = @At("TAIL")
	)
	public void keyPress(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
	{
		if(window == this.minecraft.getWindow().getWindow())
		{
			KeyHandler.onKeyInput();
		}
	}
}
