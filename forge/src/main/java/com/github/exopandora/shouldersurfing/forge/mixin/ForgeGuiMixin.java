package com.github.exopandora.shouldersurfing.forge.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import net.minecraft.client.CameraType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {
	@Redirect(
		method = "renderSpyglassOverlay",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType) {
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}
