package com.teamderpy.shouldersurfing.util;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
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
	@Nullable
	public static Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
	{
		Vector4f vec = new Vector4f((float) position.getX(), (float) position.getY(), (float) position.getZ(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.getW() == 0.0F)
		{
			return null;
		}
		
		vec.setW((1.0F / vec.getW()) * 0.5F);
		vec.setX(vec.getX() * vec.getW() + 0.5F);
		vec.setY(vec.getY() * vec.getW() + 0.5F);
		vec.setZ(vec.getZ() * vec.getW() + 0.5F);
		
		Vec2f result = new Vec2f(vec.getX() * Minecraft.getInstance().getMainWindow().getWidth(), vec.getY() * Minecraft.getInstance().getMainWindow().getHeight());
		
		if(result == null || Float.isInfinite(result.getX()) || Float.isInfinite(result.getY()))
		{
			return null;
		}
		
		return result;
	}
	
	public static double calcCameraDistance(ActiveRenderInfo info, World world, double distance)
	{
		Vector3d view = info.getProjectedView();
		Vector3d cameraOffset = ShoulderSurfingHelper.calcCameraOffset(info, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vector3d head = view.add(offset);
			Vector3d camera = head.add(cameraOffset);
			
			RayTraceContext context = new RayTraceContext(head, camera, BlockMode.COLLIDER, FluidMode.NONE, info.getRenderViewEntity());
			RayTraceResult result = world.rayTraceBlocks(context);
			
			if(result != null)
			{
				double newDistance = result.getHitVec().distanceTo(view);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	public static Optional<RayTraceResult> traceFromEyes(Entity renderView, PlayerController playerController, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(playerController.getBlockReachDistance(), playerReachOverride);
		
		RayTraceResult blockTrace = renderView.pick(blockReach, partialTicks, false);
		Vector3d eyes = renderView.getEyePosition(partialTicks);
		
		boolean extendedReach = false;
		double entityReach = blockReach;
		
		if(playerController.extendedReach())
		{
			entityReach = Math.max(6.0D, playerReachOverride);
			blockReach = entityReach;
		}
		else if(blockReach > 3.0D)
		{
			extendedReach = true;
		}
		
		entityReach = entityReach * entityReach;
		
		if(blockTrace != null)
		{
			entityReach = blockTrace.getHitVec().squareDistanceTo(eyes);
		}
		
		Vector3d look = renderView.getLook(1.0F);
		Vector3d end = eyes.add(look.scale(blockReach));
		
		AxisAlignedBB aabb = renderView.getBoundingBox().expand(look.scale(blockReach)).grow(1.0D, 1.0D, 1.0D);
		EntityRayTraceResult entityTrace = ProjectileHelper.rayTraceEntities(renderView, eyes, end, aabb, entity -> !entity.isSpectator() && entity.canBeCollidedWith(), entityReach);
		
		if(entityTrace != null)
		{
			double distanceSq = eyes.squareDistanceTo(entityTrace.getHitVec());
			
			if(extendedReach && distanceSq > 9.0D)
			{
				return Optional.empty();
			}
			else if(distanceSq < entityReach || blockTrace == null)
			{
				return Optional.of(entityTrace);
			}
		}
		
		return Optional.of(blockTrace);
	}
	
	public static Pair<Vector3d, Vector3d> calcShoulderSurfingLook(ActiveRenderInfo info, Entity entity, float partialTicks, double distanceSq)
	{
		Vector3d cameraOffset = ShoulderSurfingHelper.calcCameraOffset(info, ClientEventHandler.cameraDistance);
		Vector3d offset = ShoulderSurfingHelper.calcRayTraceHeadOffset(info, cameraOffset);
		Vector3d start = entity.getEyePosition(partialTicks).add(cameraOffset);
		Vector3d look = entity.getLook(partialTicks);
		
		if(Config.CLIENT.limitPlayerReach() && offset.lengthSquared() < distanceSq)
		{
			distanceSq -= offset.lengthSquared();
		}
		
		double distance = MathHelper.sqrt(distanceSq) + cameraOffset.distanceTo(offset);
		Vector3d end = start.add(look.scale(distance));
		
		return Pair.of(start, end);
	}
	
	public static Vector3d calcCameraOffset(@Nonnull ActiveRenderInfo info, double distance)
	{
		double dX = info.getUpVector().getX() * Config.CLIENT.getOffsetY() + info.left.getX() * Config.CLIENT.getOffsetX() + info.getViewVector().getX() * -Config.CLIENT.getOffsetZ();
		double dY = info.getUpVector().getY() * Config.CLIENT.getOffsetY() + info.left.getY() * Config.CLIENT.getOffsetX() + info.getViewVector().getY() * -Config.CLIENT.getOffsetZ();
		double dZ = info.getUpVector().getZ() * Config.CLIENT.getOffsetY() + info.left.getZ() * Config.CLIENT.getOffsetX() + info.getViewVector().getZ() * -Config.CLIENT.getOffsetZ();
		
		return new Vector3d(dX, dY, dZ).normalize().scale(distance);
	}
	
	public static Vector3d calcRayTraceHeadOffset(@Nonnull ActiveRenderInfo info, Vector3d cameraOffset)
	{
		Vector3d view = new Vector3d(info.getViewVector());
		return ShoulderSurfingHelper.lineIntersection(Vector3d.ZERO, view, cameraOffset, view);
	}
	
	public static Vector3d lineIntersection(Vector3d planePoint, Vector3d planeNormal, Vector3d linePoint, Vector3d lineNormal)
	{
		double distance = (planeNormal.dotProduct(planePoint) - planeNormal.dotProduct(linePoint)) / planeNormal.dotProduct(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final PlayerEntity player = Minecraft.getInstance().player;
		
		if(player != null)
		{
			Item item = player.getActiveItemStack().getItem();
			
			if(ItemModelsProperties.func_239417_a_(item, new ResourceLocation("pull")) != null || ItemModelsProperties.func_239417_a_(item, new ResourceLocation("throwing")) != null)
			{
				return true;
			}
			
			for(ItemStack held : player.getHeldEquipment())
			{
				if(ItemModelsProperties.func_239417_a_(held.getItem(), new ResourceLocation("charged")) != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void setPerspective(Perspective perspective)
	{
		Minecraft.getInstance().gameSettings.setPointOfView(perspective.getPointOfView());
		ShoulderSurfing.shoulderSurfing = (perspective == Perspective.SHOULDER_SURFING);
	}
	
	public static boolean doShoulderSurfing()
	{
		return Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.THIRD_PERSON_BACK && ShoulderSurfing.shoulderSurfing;
	}
}
