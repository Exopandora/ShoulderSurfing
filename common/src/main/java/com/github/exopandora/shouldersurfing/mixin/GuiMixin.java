package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
	@Shadow
	private @Final Minecraft minecraft;
	
	@Redirect(
		method = "extractCrosshair",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType) {
		return IShoulderSurfing.getInstance().getCrosshairRenderer().isCrosshairVisible();
	}
	
	@Redirect(
		method = "extractCrosshair",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/gui/components/debug/DebugScreenEntryList.isCurrentlyEnabled(Lnet/minecraft/resources/Identifier;)Z"
		)
	)
	private boolean doRenderCrosshair(DebugScreenEntryList debugScreenEntryList, Identifier identifier) {
		return debugScreenEntryList.isCurrentlyEnabled(identifier) &&
			!IShoulderSurfing.getInstance().getCrosshairRenderer().isCrosshairDynamic();
	}
	
	@Redirect(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType) {
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current() && this.minecraft.player.isScoping();
	}
}
