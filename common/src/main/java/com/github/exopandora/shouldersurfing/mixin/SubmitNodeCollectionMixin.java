package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "submitModel",
		index = 7,
		argsOnly = true
	)
	public int submitModel(int tintedColor) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(tintedColor);
	}
	
	@ModifyVariable(
		at = @At("HEAD"),
		method = "submitModelPart",
		index = 9,
		argsOnly = true
	)
	public int submitModelPart(int tintedColor) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(tintedColor);
	}
}
