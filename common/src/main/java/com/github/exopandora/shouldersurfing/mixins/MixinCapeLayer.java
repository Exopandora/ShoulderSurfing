package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.util.ResourceLocation;

@Mixin(value = CapeLayer.class, priority = 500 /* apply before capes mod */)
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
		return Config.CLIENT.isPlayerTransparencyEnabled() ? RenderType.itemEntityTranslucentCull(texture) : RenderType.entitySolid(texture);
	}
}
