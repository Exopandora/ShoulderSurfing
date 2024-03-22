package com.github.exopandora.shouldersurfing.compatibility.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.coderbot.batchedentityrendering.impl.BlendingStateHolder;
import net.coderbot.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

@Mixin(value = Sheets.class, priority = 1001 /* apply after iris */)
public class MixinSheets
{
	@Shadow
	@Final
	private static RenderType ARMOR_TRIMS_SHEET_TYPE;
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void setSheet(CallbackInfo ci)
	{
		((BlendingStateHolder) ARMOR_TRIMS_SHEET_TYPE).setTransparencyType(TransparencyType.DECAL);
	}
}
