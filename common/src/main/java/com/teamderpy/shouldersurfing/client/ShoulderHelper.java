package com.teamderpy.shouldersurfing.client;

import com.mojang.math.Vector3f;
import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.CrosshairType;
import com.teamderpy.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class ShoulderHelper
{
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
	public static ShoulderLook shoulderSurfingLook(Camera camera, Entity entity, float partialTick, double distanceSq)
	{
		Vec3 cameraOffset = camera.getPosition().subtract(entity.getEyePosition(partialTick));
		Vec3 headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vec3 cameraPos = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		
		if(Config.CLIENT.limitPlayerReach() && headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vec3 traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vec3 calcRayTraceHeadOffset(@NotNull Camera camera, Vec3 cameraOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vec3.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vec3 calcPlaneWithLineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static HitResult traceBlocksAndEntities(Camera camera, MultiPlayerGameMode gameMode, double playerReachOverride, ClipContext.Fluid fluidContext, float partialTick, boolean traceEntities, boolean doOffsetTrace)
	{
		Entity entity = camera.getEntity();
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		HitResult blockHit = traceBlocks(camera, entity, fluidContext, playerReach, partialTick, doOffsetTrace);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		if(blockHit.getType() != HitResult.Type.MISS)
		{
			playerReach = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityHitResult entityHit = traceEntities(camera, entity, playerReach, partialTick, doOffsetTrace);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());
			
			if(distance < playerReach || blockHit.getType() != HitResult.Type.MISS)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	public static EntityHitResult traceEntities(Camera camera, Entity entity, double playerReach, float partialTick, boolean doOffsetTrace)
	{
		double playerReachSq = playerReach * playerReach;
		Vec3 viewVector = entity.getViewVector(1.0F)
			.scale(playerReach);
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		AABB aabb = entity.getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, playerReachSq);
			Vec3 from = eyePosition.add(look.headOffset());
			Vec3 to = look.traceEndPos();
			aabb = aabb.move(look.headOffset());
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, from.distanceToSqr(to));
		}
		else
		{
			Vec3 from = eyePosition;
			Vec3 to = from.add(viewVector);
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, playerReachSq);
		}
	}
	
	public static BlockHitResult traceBlocks(Camera camera, Entity entity, ClipContext.Fluid fluidContext, double distance, float partialTick, boolean doOffsetTrace)
	{
		if(doOffsetTrace)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, distance * distance);
			Vec3 from = camera.getPosition();
			Vec3 to = look.traceEndPos();
			return entity.level.clip(new ClipContext(from, to, ShoulderInstance.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE, fluidContext, entity));
		}
		else
		{
			Vec3 from = entity.getEyePosition(partialTick);
			Vec3 view = new Vec3(camera.getLookVector());
			Vec3 to = from.add(view.scale(distance));
			ClipContext.Block blockContext = Config.CLIENT.getCrosshairType() == CrosshairType.DYNAMIC ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER;
			return entity.level.clip(new ClipContext(from, to, blockContext, fluidContext, entity));
		}
	}
	
	public static boolean isHoldingAdaptiveItem()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.cameraEntity instanceof LivingEntity entity)
		{
			boolean result = isHoldingAdaptiveItemInternal(minecraft, entity);
			
			for(IAdaptiveItemCallback adaptiveItemCallback : ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks())
			{
				result |= adaptiveItemCallback.isHoldingAdaptiveItem(minecraft, entity);
			}
			
			return result;
		}
		
		return false;
	}
	
	private static boolean isHoldingAdaptiveItemInternal(Minecraft minecraft, LivingEntity entity)
	{
		Item useItem = entity.getUseItem().getItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		
		if(useItems.contains(Registry.ITEM.getKey(useItem).toString()))
		{
			return true;
		}
		
		for(String useItemProperty : useItemProperties)
		{
			if(ItemProperties.getProperty(useItem, new ResourceLocation(useItemProperty)) != null)
			{
				return true;
			}
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			Item handItem = handStack.getItem();
			
			if(holdItems.contains(Registry.ITEM.getKey(handItem).toString()))
			{
				return true;
			}
			
			for(String holdItemProperty : holdItemProperties)
			{
				if(ItemProperties.getProperty(handItem, new ResourceLocation(holdItemProperty)) != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static double angle(Vector3f a, Vector3f b)
	{
		return Math.acos(a.dot(b) / (length(a) * length(b)));
	}
	
	public static double length(Vector3f vec)
	{
		return Mth.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
	
	public static record ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset) {}
}
