package com.teamderpy.shouldersurfing.client;

import java.util.List;

import javax.annotation.Nonnull;

import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ShoulderHelper
{
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	public static ShoulderLook shoulderSurfingLook(Camera camera, Entity entity, float partialTicks, double distanceSq)
	{
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(camera, ShoulderRenderer.getInstance().getCameraDistance());
		Vec3 headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vec3 cameraPos = entity.getEyePosition(partialTicks).add(cameraOffset);
		Vec3 viewVector = entity.getViewVector(partialTicks);
		
		if(Config.CLIENT.limitPlayerReach() && headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3 traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vec3 calcCameraOffset(@Nonnull Camera camera, double distance)
	{
		double dX = camera.getUpVector().x() * Config.CLIENT.getOffsetY() + camera.getLeftVector().x() * Config.CLIENT.getOffsetX() + camera.getLookVector().x() * -Config.CLIENT.getOffsetZ();
		double dY = camera.getUpVector().y() * Config.CLIENT.getOffsetY() + camera.getLeftVector().y() * Config.CLIENT.getOffsetX() + camera.getLookVector().y() * -Config.CLIENT.getOffsetZ();
		double dZ = camera.getUpVector().z() * Config.CLIENT.getOffsetY() + camera.getLeftVector().z() * Config.CLIENT.getOffsetX() + camera.getLookVector().z() * -Config.CLIENT.getOffsetZ();
		return new Vec3(dX, dY, dZ).normalize().scale(distance);
	}
	
	public static Vec3 calcRayTraceHeadOffset(@Nonnull Camera camera, Vec3 cameraOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vec3.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vec3 calcPlaneWithLineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	@SuppressWarnings("resource")
	public static boolean isHoldingSpecialItem()
	{
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		
		if(cameraEntity instanceof Player player)
		{
			List<? extends String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			Item current = player.getUseItem().getItem();
			
			if(ItemProperties.getProperty(current, PULL_PROPERTY) != null || ItemProperties.getProperty(current, THROWING_PROPERTY) != null)
			{
				return true;
			}
			else if(overrides.contains(BuiltInRegistries.ITEM.getKey(current).toString()))
			{
				return true;
			}
			
			for(ItemStack stack : player.getHandSlots())
			{
				Item item = stack.getItem();
				
				if(ItemProperties.getProperty(item, CHARGED_PROPERTY) != null)
				{
					return true;
				}
				else if(overrides.contains(BuiltInRegistries.ITEM.getKey(item).toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static record ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset) {}
}
