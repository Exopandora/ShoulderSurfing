package com.teamderpy.shouldersurfing.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShoulderSurfingHelper
{
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	@Nullable
	public static Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
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
	
	public static double cameraDistance(ActiveRenderInfo info, World world, double distance)
	{
		Vector3d view = info.getPosition();
		Vector3d cameraOffset = ShoulderSurfingHelper.cameraOffset(info, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vector3d head = view.add(offset);
			Vector3d camera = head.add(cameraOffset);
			
			RayTraceContext context = new RayTraceContext(head, camera, BlockMode.COLLIDER, FluidMode.NONE, info.getEntity());
			RayTraceResult result = world.clip(context);
			
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
	
	public static RayTraceResult traceFromEyes(Entity renderView, PlayerController playerController, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(playerController.getPickRange(), playerReachOverride);
		
		RayTraceResult blockTrace = renderView.pick(blockReach, partialTicks, false);
		Vector3d eyes = renderView.getEyePosition(partialTicks);
		
		double entityReach = blockReach;
		
		if(playerController.hasFarPickRange())
		{
			entityReach = Math.max(6.0D, playerReachOverride);
			blockReach = entityReach;
		}
		
		entityReach = entityReach * entityReach;
		
		if(blockTrace != null)
		{
			entityReach = blockTrace.getLocation().distanceToSqr(eyes);
		}
		
		Vector3d look = renderView.getViewVector(1.0F);
		Vector3d end = eyes.add(look.scale(blockReach));
		
		AxisAlignedBB aabb = renderView.getBoundingBox().expandTowards(look.scale(blockReach)).inflate(1.0D, 1.0D, 1.0D);
		EntityRayTraceResult entityTrace = ProjectileHelper.getEntityHitResult(renderView, eyes, end, aabb, entity -> !entity.isSpectator() && entity.isPickable(), entityReach);
		
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
	
	public static Pair<Vector3d, Vector3d> shoulderSurfingLook(ActiveRenderInfo info, Entity entity, float partialTicks, double distanceSq)
	{
		Vector3d cameraOffset = ShoulderSurfingHelper.cameraOffset(info, ShoulderState.getCameraDistance());
		Vector3d offset = ShoulderSurfingHelper.rayTraceHeadOffset(info, cameraOffset);
		Vector3d start = entity.getEyePosition(partialTicks).add(cameraOffset);
		Vector3d look = entity.getViewVector(partialTicks);
		
		if(Config.CLIENT.limitPlayerReach() && offset.lengthSqr() < distanceSq)
		{
			distanceSq -= offset.lengthSqr();
		}
		
		double distance = MathHelper.sqrt(distanceSq) + cameraOffset.distanceTo(offset);
		Vector3d end = start.add(look.scale(distance));
		
		return Pair.of(start, end);
	}
	
	public static Vector3d cameraOffset(@Nonnull ActiveRenderInfo info, double distance)
	{
		double dX = info.getUpVector().x() * Config.CLIENT.getOffsetY() + info.left.x() * Config.CLIENT.getOffsetX() + info.getLookVector().x() * -Config.CLIENT.getOffsetZ();
		double dY = info.getUpVector().y() * Config.CLIENT.getOffsetY() + info.left.y() * Config.CLIENT.getOffsetX() + info.getLookVector().y() * -Config.CLIENT.getOffsetZ();
		double dZ = info.getUpVector().z() * Config.CLIENT.getOffsetY() + info.left.z() * Config.CLIENT.getOffsetX() + info.getLookVector().z() * -Config.CLIENT.getOffsetZ();
		
		return new Vector3d(dX, dY, dZ).normalize().scale(distance);
	}
	
	public static Vector3d rayTraceHeadOffset(@Nonnull ActiveRenderInfo info, Vector3d cameraOffset)
	{
		Vector3d view = new Vector3d(info.getLookVector());
		return ShoulderSurfingHelper.lineIntersection(Vector3d.ZERO, view, cameraOffset, view);
	}
	
	public static Vector3d lineIntersection(Vector3d planePoint, Vector3d planeNormal, Vector3d linePoint, Vector3d lineNormal)
	{
		double distance = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final PlayerEntity player = Minecraft.getInstance().player;
		
		if(player != null)
		{
			List<? extends String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			Item current = player.getUseItem().getItem();
			
			if(ItemModelsProperties.getProperty(current, PULL_PROPERTY) != null || ItemModelsProperties.getProperty(current, THROWING_PROPERTY) != null)
			{
				return true;
			}
			else if(overrides.contains(current.getRegistryName().toString()))
			{
				return true;
			}
			
			for(ItemStack item : player.getHandSlots())
			{
				if(ItemModelsProperties.getProperty(item.getItem(), CHARGED_PROPERTY) != null)
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
		Minecraft.getInstance().options.setCameraType(perspective.getPointOfView());
		ShoulderState.setEnabled(Perspective.SHOULDER_SURFING.equals(perspective));
	}
	
	public static float getShadersResMul()
	{
		if(ShoulderState.doShaders())
		{
			return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
		}
		
		return 1.0F;
	}
}
