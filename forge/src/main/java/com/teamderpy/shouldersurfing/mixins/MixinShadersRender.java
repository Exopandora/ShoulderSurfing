package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
	private static void updateActiveRenderInfo(Camera camera, Minecraft minecraft, float partialTick, CallbackInfo ci)
	{
		ShoulderRenderer.getInstance().offsetCamera(camera, minecraft.level, partialTick);
	}
}
