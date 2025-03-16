package com.github.exopandora.shouldersurfing.compat.mixins.emf;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube")
public class MixinEMFModelPartCustom$EMFCube
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = {"compile", "method_32089", "m_171332_"},
		index = 8,
		argsOnly = true,
		remap = false
	)
	private float compile(float alpha)
	{
		return ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(alpha);
	}
}
