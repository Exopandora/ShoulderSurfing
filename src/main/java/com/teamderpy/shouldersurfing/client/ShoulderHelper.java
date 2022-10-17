package com.teamderpy.shouldersurfing.client;

import java.util.List;

import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderHelper
{
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	public static ShoulderLook shoulderSurfingLook(Entity entity, float partialTicks, double distanceSq)
	{
		Vec3d cameraOffset = ShoulderHelper.calcCameraOffset(ShoulderRenderer.getInstance().getCameraDistance(), entity.rotationYaw, entity.rotationPitch);
		Vec3d headOffset = ShoulderHelper.calcRayTraceHeadOffset(cameraOffset);
		Vec3d cameraPos = entity.getPositionEyes(partialTicks).add(cameraOffset);
		Vec3d viewVector = entity.getLook(partialTicks);
		double length = headOffset.lengthVector(); //1.9 compatibility
		length *= length;
		
		if(Config.CLIENT.limitPlayerReach() && length < distanceSq)
		{
			distanceSq -= length;
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3d traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static class ShoulderLook
	{
		private final Vec3d cameraPos;
		private final Vec3d traceEndPos;
		private final Vec3d headOffset;
		
		public ShoulderLook(Vec3d cameraPos, Vec3d traceEndPos, Vec3d headOffset)
		{
			this.cameraPos = cameraPos;
			this.traceEndPos = traceEndPos;
			this.headOffset = headOffset;
		}
		
		public Vec3d cameraPos()
		{
			return this.cameraPos;
		}
		
		public Vec3d traceEndPos()
		{
			return this.traceEndPos;
		}
		
		public Vec3d headOffset()
		{
			return this.headOffset;
		}
	}
	
	public static Vec3d calcCameraOffset(double distance, float yaw, float pitch)
	{
		return new Vec3d(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ())
				.rotatePitch((float) Math.toRadians(-pitch))
				.rotateYaw((float) Math.toRadians(-yaw))
				.normalize()
				.scale(distance);
	}
	
	public static Vec3d calcRayTraceHeadOffset(Vec3d cameraOffset)
	{
		Vec3d view = Minecraft.getMinecraft().getRenderViewEntity().getLookVec();
		return ShoulderHelper.calcPlaneWithLineIntersection(Vec3d.ZERO, view, cameraOffset, view);
	}
	
	public static Vec3d calcPlaneWithLineIntersection(Vec3d planePoint, Vec3d planeNormal, Vec3d linePoint, Vec3d lineNormal)
	{
		double distance = (planeNormal.dotProduct(planePoint) - planeNormal.dotProduct(linePoint)) / planeNormal.dotProduct(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		
		if(player != null)
		{
			List<String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			ItemStack stack = player.getActiveItemStack();
			
			if(stack != null)
			{
				Item current = stack.getItem();
				
				if(current.getPropertyGetter(PULL_PROPERTY) != null || current.getPropertyGetter(THROWING_PROPERTY) != null)
				{
					return true;
				}
				else if(overrides.contains(current.getRegistryName().toString()))
				{
					return true;
				}
			}
			
			for(ItemStack item : player.getHeldEquipment())
			{
				if(item != null)
				{
					if(item.getItem().getPropertyGetter(CHARGED_PROPERTY) != null)
					{
						return true;
					}
					else if(overrides.contains(item.getItem().getRegistryName().toString()))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
