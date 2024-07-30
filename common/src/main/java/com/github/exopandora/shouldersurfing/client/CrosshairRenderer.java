package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class CrosshairRenderer implements ICrosshairRenderer
{
	private final ShoulderSurfingImpl instance;
	private Vec2f projected;
	
	public CrosshairRenderer(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
		this.init();
	}
	
	private void init()
	{
		this.projected = null;
	}
	
	public boolean preRenderCrosshair(PoseStack poseStack, Window window)
	{
		boolean isCrosshairOutOfBounds = this.projected == null;
		
		if(!isCrosshairOutOfBounds && this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()))
		{
			Vec2f screenSize = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f center = screenSize.divide(2);
			Vec2f offset = this.projected.subtract(center).divide((float) window.getGuiScale());
			
			poseStack.pushPose();
			poseStack.last().pose().translate(offset.x(), -offset.y(), 0F);
		}
		
		return isCrosshairOutOfBounds;
	}
	
	public boolean postRenderCrosshair(PoseStack poseStack)
	{
		boolean isCrosshairOutOfBounds = this.projected == null;
		
		if(!isCrosshairOutOfBounds && this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()))
		{
			poseStack.popPose();
		}
		
		return isCrosshairOutOfBounds;
	}
	
	public void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(this.instance.isShoulderSurfing() && Minecraft.getInstance().player != null)
		{
			double interactionRangeOverride = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
			PickContext pickContext = new PickContext.Builder(camera).dynamicTrace().build();
			HitResult hitResult = this.instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, gameMode);
			Vec3 position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	@Override
	public boolean isCrosshairDynamic(Entity entity)
	{
		return this.instance.isShoulderSurfing() && Config.CLIENT.getCrosshairType().isDynamic(entity, this.instance.isAiming());
	}
	
	public void resetState()
	{
		this.init();
	}
	
	private static @Nullable Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
	{
		Window window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getScreenWidth();
		int screenHeight = window.getScreenHeight();
		
		if(screenWidth == 0 || screenHeight == 0)
		{
			return null;
		}
		
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.mul(modelView);
		vec.mul(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		float w = (1.0F / vec.w()) * 0.5F;
		float x = (vec.x() * w + 0.5F) * screenWidth;
		float y = (vec.y() * w + 0.5F) * screenHeight;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y) || w < 0.0F)
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
}
