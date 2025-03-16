package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

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
		
		this.isRenderingCameraEntity = true;
		
		if(this.shouldRenderCameraEntityTransparent(entity))
		{
			Vector3d renderOffset = this.instance.getCamera().getRenderOffset();
			float xAlpha = (float) MathHelper.clamp(Math.abs(renderOffset.x()) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			
			if(renderOffset.y() > 0)
			{
				yAlpha = (float) MathHelper.clamp(renderOffset.y() / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			}
			else if(renderOffset.y() < 0)
			{
				yAlpha = (float) MathHelper.clamp(-renderOffset.y() / -entity.getEyeHeight(), 0, 1.0F);
			}
			
			this.cameraEntityAlpha = MathHelper.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), 0.15F, 1.0F);
		}
		else
		{
			this.cameraEntityAlpha = 1.0F;
		}
		
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
				camera.getXRot() < Config.CLIENT.getHidePlayerWhenLookingUpAngle() - 90);
	}
	
	private boolean shouldRenderCameraEntityTransparent(Entity entity)
	{
		Vector3d renderOffset = this.instance.getCamera().getRenderOffset();
		return this.instance.isShoulderSurfing() && Config.CLIENT.isPlayerTransparencyEnabled() &&
			!entity.isSpectator() && (Math.abs(renderOffset.x()) < (entity.getBbWidth() / 2.0D) &&
			(renderOffset.y() >= 0 && renderOffset.y() < entity.getBbHeight() - entity.getEyeHeight() ||
				renderOffset.y() <= 0 && -renderOffset.y() < entity.getEyeHeight()));
	}
	
	public float applyCameraEntityAlphaContextAware(float alpha)
	{
		return this.isRenderingCameraEntity ? Math.min(alpha, this.cameraEntityAlpha) : alpha;
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
		return MathHelper.floor(this.cameraEntityAlpha * 255.0F);
	}
}
