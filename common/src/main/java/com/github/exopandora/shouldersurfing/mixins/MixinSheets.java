package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingRenderTypes;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.util.Util;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public class MixinSheets
{
	@Inject
	(
		at = @At("HEAD"),
		method = "armorTrimsSheet",
		cancellable = true
	)
	private static void armorTrimsSheet(boolean decal, CallbackInfoReturnable<RenderType> cir)
	{
		if(!decal && Config.CLIENT.isPlayerTransparencyEnabled())
		{
			if(Util.isImprovedTransparencyEnabled() && !Util.isCameraEntityRidingBoat())
			{
				cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucentItemTarget(Sheets.ARMOR_TRIMS_SHEET));
			}
			else
			{
				cir.setReturnValue(RenderTypes.armorTranslucent(Sheets.ARMOR_TRIMS_SHEET));
			}
		}
	}
}
