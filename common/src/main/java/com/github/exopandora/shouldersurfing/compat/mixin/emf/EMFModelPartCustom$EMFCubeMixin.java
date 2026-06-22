package com.github.exopandora.shouldersurfing.compat.mixin.emf;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube")
public class EMFModelPartCustom$EMFCubeMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = {"compile", "method_32089", "m_171332_"},
		index = 0,
		argsOnly = true,
		remap = false
	)
	private float compile(float color) {
		return ShoulderSurfing.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(color);
	}
}
