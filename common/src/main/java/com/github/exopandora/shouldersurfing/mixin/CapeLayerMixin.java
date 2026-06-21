package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CapeLayer.class, priority = 500 /* apply before capes mod */)
public class CapeLayerMixin {
	@Redirect(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType.entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		),
		require = 0
	)
	private RenderType entitySolid(ResourceLocation texture) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			return RenderType.itemEntityTranslucentCull(texture);
		}
		return RenderType.entitySolid(texture);
	}
}
