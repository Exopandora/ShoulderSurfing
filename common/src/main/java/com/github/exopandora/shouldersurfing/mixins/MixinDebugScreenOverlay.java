package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay
{
	@Inject
	(
		at = @At("RETURN"),
		method = "getGameInformation"
	)
	private void getGameInformation(CallbackInfoReturnable<List<String>> cir)
	{
		ShoulderRenderer.getInstance().appendDebugText(cir.getReturnValue());
	}
}
