package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModelRenderer.class, priority = 500 /* apply before sodium and iris */)
public class MixinModelRenderer
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V",
		index = 8,
		argsOnly = true
	)
	public float render(float alpha)
	{
		return ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().applyCameraEntityAlphaContextAware(alpha);
	}
}
