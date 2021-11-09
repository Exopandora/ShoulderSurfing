package com.teamderpy.shouldersurfing.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShoulderSurfingHelper
{
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	@Nullable
	public static Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
	{
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		vec.setW((1.0F / vec.w()) * 0.5F);
		vec.setX(vec.x() * vec.w() + 0.5F);
		vec.setY(vec.y() * vec.w() + 0.5F);
		vec.setZ(vec.z() * vec.w() + 0.5F);
		
		float x = vec.x() * Minecraft.getInstance().getWindow().getScreenWidth();
		float y = vec.y() * Minecraft.getInstance().getWindow().getScreenHeight();
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y))
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
	
	public static double cameraDistance(Camera camera, Level level, double distance)
	{
		Vec3 view = camera.getPosition();
		Vec3 cameraOffset = ShoulderSurfingHelper.cameraOffset(camera, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vec3 head = view.add(offset);
			Vec3 cameraPosition = head.add(cameraOffset);
			
			ClipContext context = new ClipContext(head, cameraPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera.getEntity());
			HitResult result = level.clip(context);
			
			if(result != null)
			{
				double newDistance = result.getLocation().distanceTo(view);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	public static HitResult traceFromEyes(Entity renderView, MultiPlayerGameMode gameMode, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		
		HitResult blockTrace = renderView.pick(blockReach, partialTicks, false);
		Vec3 eyes = renderView.getEyePosition(partialTicks);
		double entityReach = blockReach;
		
		if(gameMode.hasFarPickRange())
		{
			entityReach = Math.max(6.0D, playerReachOverride);
			blockReach = entityReach;
		}
		
		entityReach = entityReach * entityReach;
		
		if(blockTrace != null)
		{
			entityReach = blockTrace.getLocation().distanceToSqr(eyes);
		}
		
		Vec3 look = renderView.getViewVector(1.0F);
		Vec3 end = eyes.add(look.scale(blockReach));
		AABB aabb = renderView.getBoundingBox().expandTowards(look.scale(blockReach)).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityTrace = ProjectileUtil.getEntityHitResult(renderView, eyes, end, aabb, entity -> !entity.isSpectator() && entity.isPickable(), entityReach);
		
		if(entityTrace != null)
		{
			double distanceSq = eyes.distanceToSqr(entityTrace.getLocation());
			
			if(distanceSq < entityReach || blockTrace == null)
			{
				return entityTrace;
			}
		}
		
		return blockTrace;
	}
	
	public static Pair<Vec3, Vec3> shoulderSurfingLook(Camera camera, Entity entity, float partialTicks, double distanceSq)
	{
		Vec3 cameraOffset = ShoulderSurfingHelper.cameraOffset(camera, ShoulderState.getCameraDistance());
		Vec3 offset = ShoulderSurfingHelper.rayTraceHeadOffset(camera, cameraOffset);
		Vec3 start = entity.getEyePosition(partialTicks).add(cameraOffset);
		Vec3 look = entity.getViewVector(partialTicks);
		
		if(Config.CLIENT.limitPlayerReach() && offset.lengthSqr() < distanceSq)
		{
			distanceSq -= offset.lengthSqr();
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(offset);
		Vec3 end = start.add(look.scale(distance));
		
		return Pair.of(start, end);
	}
	
	public static Vec3 cameraOffset(@Nonnull Camera camera, double distance)
	{
		double dX = camera.getUpVector().x() * Config.CLIENT.getOffsetY() + camera.left.x() * Config.CLIENT.getOffsetX() + camera.getLookVector().x() * -Config.CLIENT.getOffsetZ();
		double dY = camera.getUpVector().y() * Config.CLIENT.getOffsetY() + camera.left.y() * Config.CLIENT.getOffsetX() + camera.getLookVector().y() * -Config.CLIENT.getOffsetZ();
		double dZ = camera.getUpVector().z() * Config.CLIENT.getOffsetY() + camera.left.z() * Config.CLIENT.getOffsetX() + camera.getLookVector().z() * -Config.CLIENT.getOffsetZ();
		
		return new Vec3(dX, dY, dZ).normalize().scale(distance);
	}
	
	public static Vec3 rayTraceHeadOffset(@Nonnull Camera camera, Vec3 cameraOffset)
	{
		Vec3 view = new Vec3(camera.getLookVector());
		return ShoulderSurfingHelper.lineIntersection(Vec3.ZERO, view, cameraOffset, view);
	}
	
	public static Vec3 lineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final Player player = Minecraft.getInstance().player;
		
		if(player != null)
		{
			List<? extends String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			Item current = player.getUseItem().getItem();
			
			if(ItemProperties.getProperty(current, PULL_PROPERTY) != null || ItemProperties.getProperty(current, THROWING_PROPERTY) != null)
			{
				return true;
			}
			else if(overrides.contains(current.getRegistryName().toString()))
			{
				return true;
			}
			
			for(ItemStack item : player.getHandSlots())
			{
				if(ItemProperties.getProperty(item.getItem(), CHARGED_PROPERTY) != null)
				{
					return true;
				}
				else if(overrides.contains(item.getItem().getRegistryName().toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void setPerspective(Perspective perspective)
	{
		Minecraft.getInstance().options.setCameraType(perspective.getCameraType());
		ShoulderState.setEnabled(Perspective.SHOULDER_SURFING.equals(perspective));
	}
}
