package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.util.ResourceLocation;

@Mixin(CapeLayer.class)
public class MixinCapeLayer
{
	@Redirect
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType.entitySolid(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		),
		require = 0
	)
	private RenderType entitySolid(ResourceLocation texture)
	{
		return RenderType.itemEntityTranslucentCull(texture);
	}
}
