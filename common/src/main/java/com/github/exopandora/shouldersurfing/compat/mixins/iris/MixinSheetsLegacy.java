package com.github.exopandora.shouldersurfing.compat.mixins.iris;

import com.github.exopandora.shouldersurfing.config.Config;
import net.coderbot.batchedentityrendering.impl.BlendingStateHolder;
import net.coderbot.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Sheets.class, priority = 1001 /* apply after iris */)
public class MixinSheetsLegacy
{
	@Shadow
	private static @Final RenderType ARMOR_TRIMS_SHEET_TYPE;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void setSheet(CallbackInfo ci)
	{
		if(Config.CLIENT.isPlayerTransparencyEnabled())
		{
			((BlendingStateHolder) ARMOR_TRIMS_SHEET_TYPE).setTransparencyType(TransparencyType.DECAL);
		}
	}
}
