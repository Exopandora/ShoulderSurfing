package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinduck.OptionsDuck;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin implements OptionsDuck {
	@Shadow
	private CameraType cameraType;
	
	@Inject(
		at = @At("HEAD"),
		method = "setCameraType",
		cancellable = true
	)
	public void setCameraType(CameraType cameraType, CallbackInfo ci) {
		if (cameraType != this.cameraType) {
			boolean isShoulderSurfing = Config.CLIENT.getPerspectiveConfig().isThirdPersonReplaced() && cameraType == CameraType.THIRD_PERSON_BACK;
			Perspective newPerspective = Perspective.of(cameraType, isShoulderSurfing);
			IShoulderSurfing.getInstance().changePerspective(newPerspective);
			ci.cancel();
		}
	}
	
	@Unique
	public void shouldersurfing$setCameraTypeDirect(CameraType cameraType) {
		this.cameraType = cameraType;
	}
}
