package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CameraEntityRenderer implements ICameraEntityRenderer
{
	private final ShoulderSurfingImpl instance;
	private float cameraEntityAlpha = 1.0F;
	private boolean isRenderingCameraEntity;
	
	public CameraEntityRenderer(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick)
	{
		if(this.shouldSkipCameraEntityRendering(entity))
		{
			return true;
		}
		
		this.cameraEntityAlpha = 1.0F;
		
		if(this.instance.isShoulderSurfing() && Config.CLIENT.isPlayerTransparencyEnabled())
		{
			for(ICameraEntityTransparencyCallback callback : ShoulderSurfingRegistrar.getInstance().getCameraEntityTransparencyCallbacks())
			{
				this.cameraEntityAlpha = Math.min(Mth.clamp(callback.getCameraEntityAlpha(this.instance, entity, partialTick), 0.0F, 1.0F), this.cameraEntityAlpha);
			}
		}
		
		this.isRenderingCameraEntity = true;
		
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick)
	{
		this.isRenderingCameraEntity = false;
	}
	
	private boolean shouldSkipCameraEntityRendering(Entity cameraEntity)
	{
		ShoulderSurfingCamera camera = this.instance.getCamera();
		return this.instance.isShoulderSurfing() && !cameraEntity.isSpectator() &&
			(camera.getCameraDistance() < cameraEntity.getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier() ||
				camera.getXRot() < Config.CLIENT.getHidePlayerWhenLookingUpAngle() - 90 ||
				cameraEntity instanceof Player player && player.isScoping());
	}
	
	public int applyCameraEntityAlphaContextAware(int color)
	{
		return this.isRenderingCameraEntity ? this.applyCameraEntityAlpha(color) : color;
	}
	
	public int applyCameraEntityAlpha(int color)
	{
		int cameraEntityAlpha = this.getCameraEntityAlphaAsInt();
		int alpha = FastColor.ABGR32.alpha(color);
		
		if(cameraEntityAlpha < alpha)
		{
			return FastColor.ABGR32.transparent(color) + (cameraEntityAlpha << 24);
		}
		
		return color;
	}
	
	@Override
	public boolean isRenderingCameraEntity()
	{
		return this.isRenderingCameraEntity;
	}
	
	@Override
	public float getCameraEntityAlpha()
	{
		return this.cameraEntityAlpha;
	}
	
	@Override
	public int getCameraEntityAlphaAsInt()
	{
		return FastColor.as8BitChannel(this.cameraEntityAlpha);
	}
}
