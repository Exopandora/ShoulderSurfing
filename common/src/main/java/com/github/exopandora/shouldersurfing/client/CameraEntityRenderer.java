package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CameraEntityRenderer implements ICameraEntityRenderer
{
	private final ShoulderSurfingImpl instance;
	private float cameraEntityAlpha = 1.0F;
	private boolean isRenderingCameraEntity;
	private EntityRenderState cameraEntityRenderState;
	
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
		
		if(this.instance.isShoulderSurfing() && Config.CLIENT.getPlayerConfig().isPlayerTransparencyEnabled())
		{
			this.cameraEntityAlpha = CallbackHelper.getCameraEntityAlpha(this.instance, entity, this.cameraEntityAlpha, partialTick);
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
		return this.instance.isShoulderSurfing() && !cameraEntity.isSpectator() && (camera.getCameraDistance() < cameraEntity.getBbWidth() * Config.CLIENT.getCameraConfig().keepCameraOutOfHeadMultiplier() || camera.getXRot() < Config.CLIENT.getPlayerConfig().getHidePlayerWhenLookingUpAngle() - 90 || cameraEntity instanceof Player player && player.isScoping());
	}
	
	public int applyCameraEntityAlphaContextAware(int color)
	{
		return this.isRenderingCameraEntity ? this.applyCameraEntityAlpha(color) : color;
	}
	
	public int applyCameraEntityAlpha(int color)
	{
		int cameraEntityAlpha = this.getCameraEntityAlphaAsInt();
		int alpha = ARGB.alpha(color);
		
		if(cameraEntityAlpha < alpha)
		{
			return ARGB.transparent(color) + (cameraEntityAlpha << 24);
		}
		
		return color;
	}
	
	public EntityRenderState getCameraEntityRenderState()
	{
		return this.cameraEntityRenderState;
	}
	
	public void setCameraEntityRenderState(EntityRenderState cameraEntityRenderState)
	{
		this.cameraEntityRenderState = cameraEntityRenderState;
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
		return ARGB.as8BitChannel(this.cameraEntityAlpha);
	}
}
