package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
			Vec3 renderOffset = this.instance.getCamera().getRenderOffset();
			float xAlpha = (float) Mth.clamp(Math.abs(renderOffset.x()) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			
			if(renderOffset.y() > 0)
			{
				yAlpha = (float) Mth.clamp(renderOffset.y() / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			}
			else if(renderOffset.y() < 0)
			{
				yAlpha = (float) Mth.clamp(-renderOffset.y() / -entity.getEyeHeight(), 0, 1.0F);
			}
			
			this.cameraEntityAlpha = Mth.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), 0.15F, 1.0F);
		}
		else
		{
			this.cameraEntityAlpha = 1.0F;
		}
		
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick)
	{
		this.isRenderingCameraEntity = true;
	}
	
	private boolean shouldSkipCameraEntityRendering(Entity cameraEntity)
	{
		ShoulderSurfingCamera camera = this.instance.getCamera();
		return this.instance.isShoulderSurfing() && !cameraEntity.isSpectator() &&
			(camera.getCameraDistance() < cameraEntity.getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier() ||
				camera.getXRot() < Config.CLIENT.getHidePlayerWhenLookingUpAngle() - 90 ||
				cameraEntity instanceof Player player && player.isScoping());
	}
	
	private boolean shouldRenderCameraEntityTransparent(Entity entity)
	{
		Vec3 renderOffset = this.instance.getCamera().getRenderOffset();
		return this.instance.isShoulderSurfing() && Config.CLIENT.isPlayerTransparencyEnabled() &&
			!entity.isSpectator() && (Math.abs(renderOffset.x()) < (entity.getBbWidth() / 2.0D) &&
			(renderOffset.y() >= 0 && renderOffset.y() < entity.getBbHeight() - entity.getEyeHeight() ||
				renderOffset.y() <= 0 && -renderOffset.y() < entity.getEyeHeight()));
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
		return Mth.floor(this.cameraEntityAlpha * 255.0F);
	}
}
