package com.github.exopandora.shouldersurfing.forge.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.optifine.shaders.ShadersRender;

@Mixin(ShadersRender.class)
public class MixinShadersRender
{
	@Inject
	(
		method = "updateActiveRenderInfo",
		at = @At("TAIL"),
		remap = false
	)
	private static void updateActiveRenderInfo(ActiveRenderInfo camera, Minecraft minecraft, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCamera(camera, minecraft.level, partialTick);
	}
}
