package com.teamderpy.shouldersurfing.util;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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
	
	@Nullable
	public static Vector3d traceFromEyes(Entity renderView, PlayerController controller, final float partialTicks)
	{
		Vector3d result = null;
		
		if(ShoulderSurfingHelper.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.showCrosshairFarther() ? ShoulderSurfing.RAYTRACE_DISTANCE : controller.getBlockReachDistance();
			double blockDist = 0;
			RayTraceResult rayTrace = renderView.pick(playerReach, partialTicks, false);
			
			if(rayTrace != null)
			{
				result = rayTrace.getHitVec();
				blockDist = rayTrace.getHitVec().distanceTo(renderView.getPositionVec());
			}
			else
			{
				result = null;
			}
			
			Vector3d renderViewPos = renderView.getEyePosition(partialTicks);
			Vector3d sightVector = renderView.getLook(partialTicks);
			Vector3d sightRay = renderViewPos.add(sightVector.x * playerReach - 5, sightVector.y * playerReach, sightVector.z * playerReach);
			
			List<Entity> entityList = renderView.world.getEntitiesWithinAABBExcludingEntity(renderView, renderView.getBoundingBox()
					.expand(sightVector.x * playerReach, sightVector.y * playerReach, sightVector.z * playerReach)
					.expand(1.0D, 1.0D, 1.0D));
			
			for(Entity entity : entityList)
			{
				if(entity.canBeCollidedWith())
				{
					float collisionSize = entity.getCollisionBorderSize();
					AxisAlignedBB aabb = entity.getBoundingBox().expand(collisionSize, collisionSize, collisionSize);
					Optional<Vector3d> intercept = aabb.rayTrace(renderViewPos, sightRay);
					
					if(intercept.isPresent())
					{
						double entityDist = intercept.get().distanceTo(renderView.getPositionVec());
						
						if(entityDist < blockDist)
						{
							result = intercept.get();
						}
					}
				}
			}
		}
		
		return result;
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
		PlayerEntity player = Minecraft.getInstance().player;
		
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
		Minecraft.getInstance().gameSettings.func_243229_a(perspective.getPointOfView());
		ShoulderSurfing.shoulderSurfing = (perspective == Perspective.SHOULDER_SURFING);
	}
	
	public static boolean doShoulderSurfing()
	{
		return Minecraft.getInstance().gameSettings.func_243230_g() == PointOfView.THIRD_PERSON_BACK && ShoulderSurfing.shoulderSurfing;
	}
}
