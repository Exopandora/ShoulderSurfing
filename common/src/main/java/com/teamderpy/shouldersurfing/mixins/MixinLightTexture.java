package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.renderer.LightTexture;

@Mixin(LightTexture.class)
public class MixinLightTexture
{
	@ModifyArg
	(
		method = "turnOnLightLayer",
		at = @At
		(
			value = "INVOKE",
			target = "com/mojang/blaze3d/systems/RenderSystem.setShaderColor(FFFF)V"
		),
		index = 3
	)
	private float getShaderColorAlpha(float alpha)
	{
		return ShoulderRenderer.getInstance().getPlayerAlpha();
	}
}
