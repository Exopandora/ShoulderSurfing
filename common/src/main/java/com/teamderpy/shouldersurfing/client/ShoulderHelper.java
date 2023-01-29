package com.teamderpy.shouldersurfing.client;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.plugin.ShoulderSurfingRegistrar;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

public class ShoulderHelper
{
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
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
	
	public static HitResult traceBlocksAndEntities(Camera camera, MultiPlayerGameMode gameMode, double playerReachOverride, ClipContext.Fluid fluidContext, float partialTick, boolean traceEntities, boolean shoulderSurfing)
	{
		Entity entity = camera.getEntity();
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		HitResult blockHit = traceBlocks(camera, entity, fluidContext, playerReach, partialTick, shoulderSurfing);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		if(blockHit != null)
		{
			playerReach = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityHitResult entityHit = traceEntities(camera, entity, playerReach, partialTick, shoulderSurfing);
		
		if(entityHit != null)
		{
			double distance = eyePosition.distanceTo(entityHit.getLocation());

			if(distance < playerReach || blockHit == null)
			{
				return entityHit;
			}
		}
		
		return blockHit;
	}
	
	public static EntityHitResult traceEntities(Camera camera, Entity entity, double playerReach, float partialTick, boolean shoulderSurfing)
	{
		double playerReachSq = playerReach * playerReach;
		Vec3 viewVector = entity.getViewVector(1.0F)
			.scale(playerReach);
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		AABB aabb = entity.getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, playerReachSq);
			Vec3 from = eyePosition.add(look.headOffset());
			Vec3 to = look.traceEndPos();
			aabb = aabb.move(camera.getPosition().subtract(eyePosition));
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, from.distanceToSqr(to));
		}
		else
		{
			Vec3 from = eyePosition;
			Vec3 to = from.add(viewVector);
			return ProjectileUtil.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, playerReachSq);
		}
	}
	
	public static BlockHitResult traceBlocks(Camera camera, Entity entity, ClipContext.Fluid fluidContext, double distance, float partialTick, boolean shoulderSurfing)
	{
		Vec3 eyePosition = entity.getEyePosition(partialTick);
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, distance * distance);
			Vec3 from = eyePosition.add(look.headOffset());
			Vec3 to = look.traceEndPos();
			return entity.level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, fluidContext, entity));
		}
		else
		{
			Vec3 from = eyePosition;
			Vec3 view = entity.getViewVector(partialTick);
			Vec3 to = from.add(view.scale(distance));
			return entity.level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, fluidContext, entity));
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
		List<? extends String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
		
		if(ItemProperties.getProperty(useItem, PULL_PROPERTY) != null || ItemProperties.getProperty(useItem, THROWING_PROPERTY) != null)
		{
			return true;
		}
		else if(overrides.contains(Registry.ITEM.getKey(useItem).toString()))
		{
			return true;
		}
		
		for(ItemStack handStack : entity.getHandSlots())
		{
			Item handItem = handStack.getItem();
			
			if(ItemProperties.getProperty(handItem, CHARGED_PROPERTY) != null)
			{
				return true;
			}
			else if(overrides.contains(Registry.ITEM.getKey(handItem).toString()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static record ShoulderLook(Vec3 cameraPos, Vec3 traceEndPos, Vec3 headOffset) {}
}
