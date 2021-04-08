package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.optifine.shaders.ShadersRender;

@Mixin(ShadersRender.class)
public class MixinShadersRender
{
	/**
	 * @author Exopandora
	 * @reason Fixes shaders for OptiFine
	 */
	@Overwrite(remap = false)
	public static void updateActiveRenderInfo(ActiveRenderInfo activeRenderInfo, Minecraft mc, float partialTicks)
	{
		activeRenderInfo.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, activeRenderInfo, partialTicks);
		activeRenderInfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
	}
}
