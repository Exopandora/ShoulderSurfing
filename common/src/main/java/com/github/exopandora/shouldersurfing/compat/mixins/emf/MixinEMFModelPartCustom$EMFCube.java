package com.github.exopandora.shouldersurfing.compat.mixins.emf;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Pseudo
@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube")
public class MixinEMFModelPartCustom$EMFCube
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = {"compile", "method_32089"},
		index = 5,
		argsOnly = true,
		remap = false
	)
	private int compile(int color)
	{
		return ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().applyCameraEntityAlpha(color);
	}
}
