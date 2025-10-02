package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.CameraEntityRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher
{
	@Inject
	(
		method = "submit",
		at = @At("HEAD"),
		cancellable = true
	)
	public <S extends EntityRenderState> void preRender(S renderState, CameraRenderState cameraRenderState, double renderX, double renderY, double renderZ, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci)
	{
		CameraEntityRenderer cameraEntityRenderer = ShoulderSurfingImpl.getInstance().getCameraEntityRenderer();
		
		if(renderState == cameraEntityRenderer.getCameraEntityRenderState())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.getCameraEntity();
			TickRateManager tickRateManager = minecraft.level.tickRateManager();
			DeltaTracker deltaTracker = minecraft.getDeltaTracker();
			float partialTick = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
			
			if(cameraEntityRenderer.preRenderCameraEntity(entity, partialTick))
			{
				ci.cancel();
			}
		}
	}
	
	@Inject
	(
		method = "submit",
		at = @At("TAIL")
	)
	public <S extends EntityRenderState> void postRender(S renderState, CameraRenderState cameraRenderState, double renderX, double renderY, double renderZ, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci)
	{
		CameraEntityRenderer cameraEntityRenderer = ShoulderSurfingImpl.getInstance().getCameraEntityRenderer();
		
		if(renderState == cameraEntityRenderer.getCameraEntityRenderState())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.getCameraEntity();
			TickRateManager tickRateManager = minecraft.level.tickRateManager();
			DeltaTracker deltaTracker = minecraft.getDeltaTracker();
			float partialTick = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
			cameraEntityRenderer.postRenderCameraEntity(entity, partialTick);
		}
	}
}
