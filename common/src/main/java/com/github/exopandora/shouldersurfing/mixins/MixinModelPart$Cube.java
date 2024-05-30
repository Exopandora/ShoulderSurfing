package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelPart.Cube.class)
public class MixinModelPart$Cube
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "compile",
		index = 8,
		argsOnly = true
	)
	public float compile(float alpha)
	{
		return Math.min(alpha, ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().getCameraEntityAlpha());
	}
}
