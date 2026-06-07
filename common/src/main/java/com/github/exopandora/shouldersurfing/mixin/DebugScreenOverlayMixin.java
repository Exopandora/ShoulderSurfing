package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.renderer.DebugScreenOverlayHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(
		method = "extractLines",
		at = @At("HEAD")
	)
	private void render(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, CallbackInfo ci) {
		DebugScreenOverlayHandler.appendDebugText(lines);
	}
}
