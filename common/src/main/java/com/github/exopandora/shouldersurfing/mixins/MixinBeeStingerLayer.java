package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeeStingerLayer.class)
public class MixinBeeStingerLayer
{
	@Redirect
	(
		method = "renderStuckItem",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/RenderType.entityCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		)
	)
	private RenderType entityCutoutNoCull(ResourceLocation texture)
	{
		return Config.CLIENT.isPlayerTransparencyEnabled() ? RenderType.entityTranslucent(texture) : RenderType.entityCutoutNoCull(texture);
	}
}
