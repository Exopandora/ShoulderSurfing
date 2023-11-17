package com.teamderpy.shouldersurfing.client;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.CrosshairType;
import com.teamderpy.shouldersurfing.plugin.ShoulderSurfingRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderHelper
{
	public static final float DEG_TO_RAD = ((float)Math.PI / 180F);
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity != null && entity.canBeCollidedWith());
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	public static ShoulderLook shoulderSurfingLook(Entity entity, float partialTick, double distanceSq)
	{
		Vec3d cameraOffset = ShoulderHelper.calcCameraOffset(ShoulderRenderer.getInstance().getCameraDistance(), entity.rotationYaw, entity.rotationPitch, partialTick);
		Vec3d headOffset = ShoulderHelper.calcRayTraceHeadOffset(cameraOffset);
		Vec3d cameraPos = entity.getPositionEyes(partialTick).add(cameraOffset);
		Vec3d viewVector = entity.getLook(partialTick);
		double length = headOffset.length(); // 1.9 compatibility
		length *= length;
		
		if(Config.CLIENT.limitPlayerReach() && length < distanceSq)
		{
			distanceSq -= length;
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3d traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vec3d calcCameraOffset(double distance, float yaw, float pitch, float partialTick)
	{
		ShoulderInstance instance = ShoulderInstance.getInstance();
		double offsetX = lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
		double offsetY = lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
		double offsetZ = lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
		return new Vec3d(offsetX, offsetY, -offsetZ)
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
	
	public static RayTraceResult traceBlocksAndEntities(Entity entity, PlayerControllerMP gameMode, double playerReachOverride, boolean stopOnFluid, float partialTick, boolean traceEntities, boolean shoulderSurfing)
	{
		double playerReach = Math.max(gameMode.getBlockReachDistance(), playerReachOverride);
		RayTraceResult blockHit = traceBlocks(entity, stopOnFluid, playerReach, partialTick, shoulderSurfing);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3d eyePosition = entity.getPositionEyes(partialTick);
		
		if(gameMode.extendedReach())
		{
			playerReach = Math.max(playerReach, gameMode.isInCreativeMode() ? 6.0D : 3.0D);
		}
		
		if(blockHit != null)
		{
			playerReach = blockHit.hitVec.distanceTo(eyePosition);
		}
		
		RayTraceResult entityHit = traceEntities(entity, playerReach, partialTick, shoulderSurfing);
		
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
	
	public static RayTraceResult traceEntities(Entity cameraEntity, double playerReach, float partialTick, boolean shoulderSurfing)
	{
		double playerReachSq = playerReach * playerReach;
		Vec3d viewVector = cameraEntity.getLook(1.0F)
			.scale(playerReach);
		Vec3d eyePosition = cameraEntity.getPositionEyes(partialTick);
		double searchDistance = Math.min(64, playerReach);
		AxisAlignedBB aabb = cameraEntity.getEntityBoundingBox()
			.expand(viewVector.x * searchDistance, viewVector.y * searchDistance, viewVector.z * searchDistance)
			.grow(1.0D, 1.0D, 1.0D);
		Vec3d from;
		Vec3d to;
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(cameraEntity, partialTick, playerReachSq);
			from = eyePosition.add(look.headOffset());
			to = look.traceEndPos();
			aabb = aabb.offset(look.headOffset());
		}
		else
		{
			from = eyePosition;
			to = from.add(viewVector);
		}
		
		List<Entity> entities = Minecraft.getMinecraft().world.getEntitiesInAABBexcluding(cameraEntity, aabb, ENTITY_IS_PICKABLE);
		Vec3d entityHitVec = null;
		Entity entityResult = null;
		double minEntityReachSq = playerReachSq;
		
		for(Entity entity : entities)
		{
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(from, to);
			
			if(axisalignedbb.contains(eyePosition))
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
					if(entity == cameraEntity.getRidingEntity() && !entity.canRiderInteract())
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
		
		return new RayTraceResult(entityResult, entityHitVec);
	}
	
	public static RayTraceResult traceBlocks(Entity entity, boolean stopOnFluid, double distance, float partialTick, boolean shoulderSurfing)
	{
		Vec3d eyePosition = entity.getPositionEyes(partialTick);
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(entity, partialTick, distance * distance);
			Vec3d from = eyePosition.add(look.headOffset());
			Vec3d to = look.traceEndPos();
			return entity.world.rayTraceBlocks(from, to, stopOnFluid, false, true);
		}
		else
		{
			Vec3d from = eyePosition;
			Vec3d view = entity.getLook(partialTick);
			Vec3d to = from.add(view.scale(distance));
			return entity.world.rayTraceBlocks(from, to, stopOnFluid, Config.CLIENT.getCrosshairType() == CrosshairType.ADAPTIVE, true);
		}
	}
	
	public static boolean isHoldingAdaptiveItem()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		
		if(minecraft.getRenderViewEntity() instanceof EntityLivingBase)
		{
			EntityLivingBase entity = (EntityLivingBase) minecraft.getRenderViewEntity();
			boolean result = isHoldingAdaptiveItemInternal(minecraft, entity);
			
			for(IAdaptiveItemCallback adaptiveItemCallback : ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks())
			{
				result |= adaptiveItemCallback.isHoldingAdaptiveItem(minecraft, entity);
			}
			
			return result;
		}
		
		return false;
	}
	
	private static boolean isHoldingAdaptiveItemInternal(Minecraft minecraft, EntityLivingBase entity)
	{
		List<? extends String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
		ItemStack useStack = entity.getActiveItemStack();
		
		if(useStack != null) // 1.9 compatibility
		{
			Item useItem = useStack.getItem();
			
			if(useItem.getPropertyGetter(PULL_PROPERTY) != null || useItem.getPropertyGetter(THROWING_PROPERTY) != null)
			{
				return true;
			}
			else if(overrides.contains(useItem.getRegistryName().toString()))
			{
				return true;
			}
		}
		
		for(ItemStack handStack : entity.getHeldEquipment())
		{
			if(handStack != null) // 1.9 compatibility
			{
				Item handItem = handStack.getItem();
				
				if(handItem.getPropertyGetter(CHARGED_PROPERTY) != null)
				{
					return true;
				}
				else if(overrides.contains(handItem.getRegistryName().toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static double lerp(double f, double a, double b)
	{
		return a + f * (b - a);
	}
	
	public static double angle(Vec3d a, Vec3d b)
	{
		return Math.acos(a.dotProduct(b) / (length(a) * length(b)));
	}
	
	public static double length(Vec3d vec)
	{
		return MathHelper.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
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
}
