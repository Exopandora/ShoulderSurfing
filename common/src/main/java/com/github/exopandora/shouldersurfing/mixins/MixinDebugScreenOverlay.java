package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.DebugScreenOverlayHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay
{
	@Inject
	(
		method = "renderLines",
		at = @At("HEAD")
	)
	private void render(GuiGraphics guiGraphics, List<String> lines, boolean leftAligned, CallbackInfo ci)
	{
		DebugScreenOverlayHandler.appendDebugText(lines);
	}
}
