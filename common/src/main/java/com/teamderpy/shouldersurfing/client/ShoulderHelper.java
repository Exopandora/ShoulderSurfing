package com.teamderpy.shouldersurfing.client;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.teamderpy.shouldersurfing.api.callback.IAdaptiveItemCallback;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.CrosshairType;
import com.teamderpy.shouldersurfing.mixins.ActiveRenderInfoAccessor;
import com.teamderpy.shouldersurfing.plugin.ShoulderSurfingRegistrar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;

public class ShoulderHelper
{
	public static final float DEG_TO_RAD = ((float)Math.PI / 180F);
	private static final Predicate<Entity> ENTITY_IS_PICKABLE = entity -> !entity.isSpectator() && entity.isPickable();
	
	public static ShoulderLook shoulderSurfingLook(ActiveRenderInfo camera, Entity entity, float partialTick, double distanceSq)
	{
		Vector3d cameraOffset = ShoulderHelper.calcCameraOffset(camera, ShoulderRenderer.getInstance().getCameraDistance(), partialTick);
		Vector3d headOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, cameraOffset);
		Vector3d cameraPos = entity.getEyePosition(partialTick).add(cameraOffset);
		Vector3d viewVector = entity.getViewVector(partialTick);
		
		if(Config.CLIENT.limitPlayerReach() && headOffset.lengthSqr() < distanceSq)
		{
			distanceSq -= headOffset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(headOffset);
		Vector3d traceEnd = cameraPos.add(viewVector.scale(distance));
		return new ShoulderLook(cameraPos, traceEnd, headOffset);
	}
	
	public static Vector3d calcCameraOffset(@NotNull ActiveRenderInfo camera, double distance, float partialTick)
	{
		ActiveRenderInfoAccessor accessor = (ActiveRenderInfoAccessor) camera;
		ShoulderInstance instance = ShoulderInstance.getInstance();
		double offsetX = MathHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
		double offsetY = MathHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
		double offsetZ = MathHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
		return new Vector3d(camera.getUpVector()).scale(offsetY)
			.add(new Vector3d(accessor.getLeft()).scale(offsetX))
			.add(new Vector3d(camera.getLookVector()).scale(-offsetZ))
			.normalize()
			.scale(distance);
	}
	
	public static Vector3d calcRayTraceHeadOffset(@NotNull ActiveRenderInfo camera, Vector3d cameraOffset)
	{
		Vector3d lookVector = new Vector3d(camera.getLookVector());
		return ShoulderHelper.calcPlaneWithLineIntersection(Vector3d.ZERO, lookVector, cameraOffset, lookVector);
	}
	
	public static Vector3d calcPlaneWithLineIntersection(Vector3d planePoint, Vector3d planeNormal, Vector3d linePoint, Vector3d lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static RayTraceResult traceBlocksAndEntities(ActiveRenderInfo camera, PlayerController gameMode, double playerReachOverride, RayTraceContext.FluidMode fluidContext, float partialTick, boolean traceEntities, boolean shoulderSurfing)
	{
		Entity entity = camera.getEntity();
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		RayTraceResult blockHit = traceBlocks(camera, entity, fluidContext, playerReach, partialTick, shoulderSurfing);
		
		if(!traceEntities)
		{
			return blockHit;
		}
		
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		if(blockHit != null)
		{
			playerReach = blockHit.getLocation().distanceTo(eyePosition);
		}
		
		EntityRayTraceResult entityHit = traceEntities(camera, entity, playerReach, partialTick, shoulderSurfing);
		
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
	
	public static EntityRayTraceResult traceEntities(ActiveRenderInfo camera, Entity entity, double playerReach, float partialTick, boolean shoulderSurfing)
	{
		double playerReachSq = playerReach * playerReach;
		Vector3d viewVector = entity.getViewVector(1.0F)
			.scale(playerReach);
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		AxisAlignedBB aabb = entity.getBoundingBox()
			.expandTowards(viewVector)
			.inflate(1.0D, 1.0D, 1.0D);
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, playerReachSq);
			Vector3d from = eyePosition.add(look.headOffset());
			Vector3d to = look.traceEndPos();
			aabb = aabb.move(look.headOffset());
			return ProjectileHelper.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, from.distanceToSqr(to));
		}
		else
		{
			Vector3d from = eyePosition;
			Vector3d to = from.add(viewVector);
			return ProjectileHelper.getEntityHitResult(entity, from, to, aabb, ENTITY_IS_PICKABLE, playerReachSq);
		}
	}
	
	public static BlockRayTraceResult traceBlocks(ActiveRenderInfo camera, Entity entity, RayTraceContext.FluidMode fluidContext, double distance, float partialTick, boolean shoulderSurfing)
	{
		Vector3d eyePosition = entity.getEyePosition(partialTick);
		
		if(shoulderSurfing)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, entity, partialTick, distance * distance);
			Vector3d from = eyePosition.add(look.headOffset());
			Vector3d to = look.traceEndPos();
			return entity.level.clip(new RayTraceContext(from, to, RayTraceContext.BlockMode.OUTLINE, fluidContext, entity));
		}
		else
		{
			Vector3d from = eyePosition;
			Vector3d view = entity.getViewVector(partialTick);
			Vector3d to = from.add(view.scale(distance));
			RayTraceContext.BlockMode blockContext = Config.CLIENT.getCrosshairType() == CrosshairType.DYNAMIC ? RayTraceContext.BlockMode.OUTLINE : RayTraceContext.BlockMode.COLLIDER;
			return entity.level.clip(new RayTraceContext(from, to, blockContext, fluidContext, entity));
		}
	}
	
	public static boolean isHoldingAdaptiveItem()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.cameraEntity instanceof LivingEntity)
		{
			LivingEntity entity = (LivingEntity) minecraft.cameraEntity;
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
			if(ItemModelsProperties.getProperty(useItem, new ResourceLocation(useItemProperty)) != null)
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
				if(ItemModelsProperties.getProperty(handItem, new ResourceLocation(holdItemProperty)) != null)
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
		return MathHelper.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
	
	public static class ShoulderLook
	{
		private final Vector3d cameraPos;
		private final Vector3d traceEndPos;
		private final Vector3d headOffset;
		
		public ShoulderLook(Vector3d cameraPos, Vector3d traceEndPos, Vector3d headOffset)
		{
			this.cameraPos = cameraPos;
			this.traceEndPos = traceEndPos;
			this.headOffset = headOffset;
		}
		
		public Vector3d cameraPos()
		{
			return this.cameraPos;
		}
		
		public Vector3d traceEndPos()
		{
			return this.traceEndPos;
		}
		
		public Vector3d headOffset()
		{
			return this.headOffset;
		}
	}
}
