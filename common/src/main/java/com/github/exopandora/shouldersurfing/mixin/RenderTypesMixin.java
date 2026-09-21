package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.renderer.rendertype.ShoulderSurfingRenderTypes;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTypes.class)
class RenderTypesMixin {
	@Inject(
		at = @At("HEAD"),
		method = "armorCutoutNoCull",
		cancellable = true
	)
	private static void armorCutoutNoCull(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucentNoCull(texture));
		}
	}
	
	@Inject(
		at = @At("HEAD"),
		method = "armorCutoutNoCullGlint",
		cancellable = true
	)
	private static void armorCutoutNoCullGlint(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucentNoCullGlint(texture));
		}
	}
	
	@Inject(
		at = @At("HEAD"),
		method = "armorTrim",
		cancellable = true
	)
	private static void armorTrim(Identifier texture, boolean decal, CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			cir.setReturnValue(ShoulderSurfingRenderTypes.armorTrimTranslucent(texture, decal));
		}
	}
	
	@Inject(
		at = @At("HEAD"),
		method = "trimmedArmorGlint",
		cancellable = true
	)
	private static void trimmedArmorGlint(CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			cir.setReturnValue(ShoulderSurfingRenderTypes.trimmedArmorGlint());
		}
	}
}
