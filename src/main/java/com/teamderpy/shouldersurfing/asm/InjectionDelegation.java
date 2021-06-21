package com.teamderpy.shouldersurfing.asm;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Predicates;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class InjectionDelegation
{
	// XXX Forge Hooks
	// public static RayTraceResult rayTraceEyes(EntityLivingBase entity, double length)
	// public static Vec3d rayTraceEyeHitVec(EntityLivingBase entity, double length)
	
	public static void cameraSetup(float x, float y, float z)
	{
		final World world = Minecraft.getMinecraft().world;
		
		if(ShoulderSurfing.STATE.doShoulderSurfing() && world != null)
		{
			Vec3d offset = new Vec3d(Config.CLIENT.getOffsetX(), -Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
			double distance = ShoulderSurfingHelper.cameraDistance(world, offset.lengthVector());
			Vec3d scaled = offset.normalize().scale(distance);
			
			ShoulderSurfing.STATE.setCameraDistance(distance);
			
			GlStateManager.translate(scaled.x, scaled.y, scaled.z);
		}
		else
		{
			GlStateManager.translate(x, y, z);
		}
	}
	
	public static int doRenderCrosshair()
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(ShoulderSurfing.STATE.isAiming()) ? 0 : 1;
	}
	
	public static RayTraceResult getRayTraceResult(World world, Vec3d vec1, Vec3d vec2)
	{
		return world.rayTraceBlocks(vec1, vec2, false, true, false);
	}
	
	public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderSurfing.STATE.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Entry<Vec3d, Vec3d> look = ShoulderSurfingHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.world.rayTraceBlocks(look.getKey(), look.getValue(), false, false, true);
		}
		
		Vec3d eyes = entity.getPositionEyes(partialTicks);
		Vec3d look = entity.getLook(partialTicks);
		Vec3d end = eyes.addVector(look.x * blockReachDistance, look.y * blockReachDistance, look.z * blockReachDistance);
		
		return entity.world.rayTraceBlocks(eyes, end, false, false, true);
	}
	
	public static void getMouseOver()
	{
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		
		if(renderView != null && Minecraft.getMinecraft().world != null)
		{
			Minecraft.getMinecraft().mcProfiler.startSection("pick");
			double blockReach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
			
			Minecraft.getMinecraft().pointedEntity = null;
			Minecraft.getMinecraft().objectMouseOver = renderView.rayTrace(blockReach, Minecraft.getMinecraft().getRenderPartialTicks());
			
			Vec3d eyes = renderView.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
			boolean extendedReach = false;
			double entityReach = blockReach;
			
			if(Minecraft.getMinecraft().playerController.extendedReach())
			{
				entityReach = 6.0D;
				blockReach = entityReach;
			}
			else if(blockReach > 3.0D)
			{
				extendedReach = true;
			}
			
			if(Minecraft.getMinecraft().objectMouseOver != null)
			{
				entityReach = Minecraft.getMinecraft().objectMouseOver.hitVec.distanceTo(eyes);
			}
			
			Entry<Vec3d, Vec3d> look = ShoulderSurfingHelper.shoulderSurfingLook(renderView, Minecraft.getMinecraft().getRenderPartialTicks(), entityReach);
			Vec3d viewlook = renderView.getLook(1.0F);
			
			Vec3d entityHitVec = null;
			List<Entity> list = Minecraft.getMinecraft().world.getEntitiesInAABBexcluding(renderView, renderView.getEntityBoundingBox().expand(viewlook.x * blockReach, viewlook.y * blockReach, viewlook.z * blockReach).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));
			Entity pointedEntity = null;
			double minEntityReach = entityReach;
			
			for(Entity entity : list)
			{
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(look.getKey(), look.getValue());
				
				if(axisalignedbb.contains(eyes))
				{
					if(minEntityReach >= 0.0D)
					{
						pointedEntity = entity;
						entityHitVec = raytraceresult == null ? eyes : raytraceresult.hitVec;
						minEntityReach = 0.0D;
					}
				}
				else if(raytraceresult != null)
				{
					double distanceSq = eyes.distanceTo(raytraceresult.hitVec);
					
					if(distanceSq < minEntityReach || minEntityReach == 0.0D)
					{
						if(entity.getLowestRidingEntity() == renderView.getLowestRidingEntity() && !entity.canRiderInteract())
						{
							if(minEntityReach == 0.0D)
							{
								pointedEntity = entity;
								entityHitVec = raytraceresult.hitVec;
							}
						}
						else
						{
							pointedEntity = entity;
							entityHitVec = raytraceresult.hitVec;
							minEntityReach = distanceSq;
						}
					}
				}
			}
			
			if(pointedEntity != null && extendedReach && eyes.distanceTo(entityHitVec) > 3.0D)
			{
				pointedEntity = null;
				Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, entityHitVec, null, new BlockPos(entityHitVec));
			}
			
			if(pointedEntity != null && (minEntityReach < entityReach || Minecraft.getMinecraft().objectMouseOver == null))
			{
				Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(pointedEntity, entityHitVec);
				
				if(pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
				{
					Minecraft.getMinecraft().pointedEntity = pointedEntity;
				}
			}
			
			Minecraft.getMinecraft().mcProfiler.endSection();
		}
	}
	
//	//MixinGameRenderer
//	public static EntityRayTraceResult getEntityHitResult(Entity shooter, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distanceSq)
//	{
//		if(ShoulderSurfingHelper.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
//		{
//			Pair<Vec3d, Vec3d> look = ShoulderSurfingHelper.calcShoulderSurfingLook(this.mainCamera, shooter, Minecraft.getInstance().getFrameTime(), distanceSq);
//			return ProjectileHelper.getEntityHitResult(shooter, look.getSecond(), look.getFirst(), boundingBox, filter, distanceSq);
//		}
//		
//		return ProjectileHelper.getEntityHitResult(shooter, startVec, endVec, boundingBox, filter, distanceSq);
//	}
//	
//	
//	//MixinShadersRender
//	public static static void updateActiveRenderInfo(ActiveRenderInfo activeRenderInfo, Minecraft mc, float partialTicks)
//	{
//		activeRenderInfo.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
//		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, activeRenderInfo, partialTicks);
//		activeRenderInfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
//	}
}
