package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.optifine.shaders.ShadersRender;

@Mixin(ShadersRender.class)
public class MixinShadersRender
{
	/**
	 * @author Exopandora
	 * @reason Fixes shaders for OptiFine
	 */
	@Overwrite(remap = false)
	public static void updateActiveRenderInfo(Camera camera, Minecraft mc, float partialTicks)
	{
		camera.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		ComputeCameraAngles cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, camera, partialTicks);
		camera.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
	}
}
