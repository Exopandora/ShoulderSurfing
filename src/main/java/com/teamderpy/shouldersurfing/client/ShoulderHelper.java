package com.teamderpy.shouldersurfing.client;

import java.util.List;

import com.teamderpy.shouldersurfing.config.Config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class ShoulderHelper
{
	private static final Vec3 ZERO = Vec3.createVectorHelper(0, 0, 0);
	
	public static ShoulderLook shoulderSurfingLook(EntityLivingBase entity, float partialTicks, double distanceSq)
	{
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(ShoulderRenderer.getInstance().getCameraDistance());
		Vec3 headOffset = ShoulderHelper.calcRayTraceHeadOffset(cameraOffset);
		Vec3 cameraPos = entity.getPosition(partialTicks).addVector(cameraOffset.xCoord, cameraOffset.yCoord, cameraOffset.zCoord);
		Vec3 viewVector = entity.getLook(partialTicks);
		
		if(Config.CLIENT.limitPlayerReach() && lengthSqr(headOffset) < distanceSq)
		{
			distanceSq -= lengthSqr(headOffset);
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3 traceEnd = cameraPos.addVector(viewVector.xCoord * distance, viewVector.yCoord * distance, viewVector.zCoord * distance);
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	private static double lengthSqr(Vec3 vec)
	{
		return vec.xCoord * vec.xCoord + vec.yCoord * vec.yCoord + vec.zCoord * vec.zCoord;
	}
	
	public static class ShoulderLook
	{
		private final Vec3 cameraPos;
		private final Vec3 traceEndPos;
		private final Vec3 headOffset;
		
		public ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset)
		{
			this.cameraPos = cameraPos;
			this.traceEndPos = traceEndPos;
			this.headOffset = headOffset;
		}
		
		public Vec3 cameraPos()
		{
			return this.cameraPos;
		}
		
		public Vec3 traceEndPos()
		{
			return this.traceEndPos;
		}
		
		public Vec3 headOffset()
		{
			return this.headOffset;
		}
	}
	
	public static Vec3 calcCameraOffset(double distance)
	{
		Vec3 result = Vec3.createVectorHelper(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
		result.rotateAroundX((float) Math.toRadians(-Minecraft.getMinecraft().renderViewEntity.rotationPitch));
		result.rotateAroundY((float) Math.toRadians(-Minecraft.getMinecraft().renderViewEntity.rotationYaw));
		result = result.normalize();
		return Vec3.createVectorHelper(result.xCoord * distance, result.yCoord * distance, result.zCoord * distance);
	}
	
	public static Vec3 calcRayTraceHeadOffset(Vec3 cameraOffset)
	{
		Vec3 view = Minecraft.getMinecraft().renderViewEntity.getLookVec();
		return ShoulderHelper.calcPlaneWithLineIntersection(ZERO, view, cameraOffset, view);
	}
	
	public static Vec3 calcPlaneWithLineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dotProduct(planePoint) - planeNormal.dotProduct(linePoint)) / planeNormal.dotProduct(lineNormal);
		return linePoint.addVector(lineNormal.xCoord * distance, lineNormal.yCoord * distance, lineNormal.zCoord * distance);
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		if(player != null)
		{
			List<String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			ItemStack stack = player.getHeldItem();
			
			if(stack != null)
			{
				Item current = stack.getItem();
				
				if(current instanceof ItemPotion && ItemPotion.isSplash(current.getDamage(stack)))
				{
					return true;
				}
				else if(overrides.contains(current.delegate.name()))
				{
					return true;
				}
			}
			
			ItemStack item = player.getItemInUse();
			
			if(item != null)
			{
				Item current = stack.getItem();
				
				if(current instanceof ItemBow)
				{
					return true;
				}
				else if(overrides.contains(current.delegate.name()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
