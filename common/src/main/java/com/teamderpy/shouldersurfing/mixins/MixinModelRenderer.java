package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.renderer.model.ModelRenderer;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer
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
		return Math.min(alpha, ShoulderRenderer.getInstance().getPlayerAlpha());
	}
}
