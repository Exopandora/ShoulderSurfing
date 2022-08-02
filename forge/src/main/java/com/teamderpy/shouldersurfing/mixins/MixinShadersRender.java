package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

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
	public static void updateActiveRenderInfo(ActiveRenderInfo camera, Minecraft minecraft, float partialTick)
	{
		ShoulderRenderer.getInstance().offsetCamera(camera, minecraft.level, partialTick);
	}
}
