package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.renderer.rendertype.ShoulderSurfingRenderTypes;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.util.Util;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(RenderTypes.class)
public class RenderTypesMixin {
	@Shadow
	private static @Final Function<Identifier, RenderType> ARMOR_TRANSLUCENT;
	
	@Inject(
		at = @At("HEAD"),
		method = "armorCutoutNoCull",
		cancellable = true
	)
	private static void armorCutoutNoCull(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			if (Util.isImprovedTransparencyEnabled() && !Util.isCameraEntityRidingBoat()) {
				cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucentItemTarget(texture));
			} else {
				cir.setReturnValue(ARMOR_TRANSLUCENT.apply(texture));
			}
		}
	}
	
	@Inject(
		at = @At("HEAD"),
		method = "armorEntityGlint",
		cancellable = true
	)
	private static void armorEntityGlint(CallbackInfoReturnable<RenderType> cir) {
		if (Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled()) {
			if (Util.isImprovedTransparencyEnabled() && !Util.isCameraEntityRidingBoat()) {
				cir.setReturnValue(ShoulderSurfingRenderTypes.armorEntityGlintItemTarget());
			}
		}
	}
}
