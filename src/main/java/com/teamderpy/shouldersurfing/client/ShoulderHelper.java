package com.teamderpy.shouldersurfing.client;

import java.util.List;

import com.teamderpy.shouldersurfing.config.Config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
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
	
	public static MovingObjectPosition traceBlocksAndEntities(EntityLivingBase entity, PlayerControllerMP gameMode, double playerReachOverride, boolean stopOnFluid, float partialTick, boolean traceEntities, boolean shoulderSurfing)
	{
		double playerReach = Math.max(gameMode.getBlockReachDistance(), playerReachOverride);
		MovingObjectPosition blockHit = traceBlocks(entity, stopOnFluid, playerReach, partialTick, shoulderSurfing);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3 eyePosition = entity.getPosition(partialTick);
		
		if(gameMode.extendedReach())
		{
			playerReach = Math.max(playerReach, gameMode.isInCreativeMode() ? 6.0D : 3.0D);
		}
		
		if(blockHit != null)
		{
			playerReach = blockHit.hitVec.distanceTo(eyePosition);
		}
		
		MovingObjectPosition entityHit = traceEntities(entity, playerReach, partialTick, shoulderSurfing);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.hitVec);

			if(distance < playerReach || blockHit == null)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	public static MovingObjectPosition traceEntities(EntityLivingBase cameraEntity, double playerReach, float partialTick, boolean shoulderSurfing)
	{
		double playerReachSq = playerReach * playerReach;
		Vec3 viewVector = cameraEntity.getLook(1.0F);
		viewVector = Vec3.createVectorHelper(viewVector.xCoord * playerReach, viewVector.yCoord * playerReach, viewVector.zCoord * playerReach);
		Vec3 eyePosition = cameraEntity.getPosition(partialTick);
		double searchDistance = Math.min(64, playerReach);
		AxisAlignedBB aabb = cameraEntity.boundingBox
			.addCoord(viewVector.xCoord * searchDistance, viewVector.yCoord * searchDistance, viewVector.zCoord * searchDistance)
			.expand(1.0D, 1.0D, 1.0D);
		Vec3 from;
		Vec3 to;
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(cameraEntity, partialTick, playerReachSq);
			from = eyePosition.addVector(look.headOffset().xCoord, look.headOffset().yCoord, look.headOffset().zCoord);
			to = look.traceEndPos();
			Vec3 aabbOffset = look.cameraPos().subtract(eyePosition);
			aabb = aabb.offset(aabbOffset.xCoord, aabbOffset.yCoord, aabbOffset.zCoord);
		}
		else
		{
			from = eyePosition;
			to = from.addVector(viewVector.xCoord, viewVector.yCoord, viewVector.zCoord);
		}
		
		List<Entity> entities = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(cameraEntity, aabb, Entity::canBeCollidedWith);
		Vec3 entityHitVec = null;
		Entity entityResult = null;
		double minEntityReachSq = playerReachSq;
		
		for(Entity entity : entities)
		{
			AxisAlignedBB axisalignedbb = entity.boundingBox.expand(entity.getCollisionBorderSize(), entity.getCollisionBorderSize(), entity.getCollisionBorderSize());
			MovingObjectPosition raytraceresult = axisalignedbb.calculateIntercept(from, to);

			if(axisalignedbb.isVecInside(eyePosition))
			{
				if(minEntityReachSq >= 0.0D)
				{
					entityResult = entity;
					entityHitVec = raytraceresult == null ? eyePosition : raytraceresult.hitVec;
					minEntityReachSq = 0.0D;
				}
			}
			else if(raytraceresult != null)
			{
				double distanceSq = eyePosition.squareDistanceTo(raytraceresult.hitVec);

				if(distanceSq < minEntityReachSq || minEntityReachSq == 0.0D)
				{
					if(entity == cameraEntity.ridingEntity && !entity.canRiderInteract())
					{
						if(minEntityReachSq == 0.0D)
						{
							entityResult = entity;
							entityHitVec = raytraceresult.hitVec;
						}
					}
					else
					{
						entityResult = entity;
						entityHitVec = raytraceresult.hitVec;
						minEntityReachSq = distanceSq;
					}
				}
			}
		}
		
		if(entityResult == null)
		{
			return null;
		}
		
		return new MovingObjectPosition(entityResult, entityHitVec);
	}
	
	public static MovingObjectPosition traceBlocks(EntityLivingBase entity, boolean stopOnFluid, double distance, float partialTick, boolean shoulderSurfing)
	{
		Vec3 eyePosition = entity.getPosition(partialTick);

		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(entity, partialTick, distance * distance);
			Vec3 from = eyePosition.addVector(look.headOffset().xCoord, look.headOffset().yCoord, look.headOffset().zCoord);
			Vec3 to = look.traceEndPos();
			return entity.worldObj.func_147447_a(from, to, stopOnFluid, false, true);
		}
		else
		{
			Vec3 from = eyePosition;
			Vec3 view = entity.getLook(partialTick);
			Vec3 to = from.addVector(view.xCoord * distance, view.yCoord * distance, view.zCoord * distance);
			return entity.worldObj.func_147447_a(from, to, stopOnFluid, false, true);
		}
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
