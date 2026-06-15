package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelPart.Cube.class)
public class ModelPart$CubeMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "compile",
		index = 8,
		argsOnly = true
	)
	public float compile(float color) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
