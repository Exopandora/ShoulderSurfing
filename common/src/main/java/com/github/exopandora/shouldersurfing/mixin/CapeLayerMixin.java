package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CapeLayer.class, priority = 500 /* apply before capes mod */)
public class CapeLayerMixin {
	@Redirect(
		method = "submit",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/client/renderer/rendertype/RenderTypes.entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		),
		require = 0
	)
	private RenderType entitySolid(Identifier texture) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			return RenderTypes.entityTranslucentCullItemTarget(texture);
		}
		return RenderTypes.entitySolid(texture);
	}
}
