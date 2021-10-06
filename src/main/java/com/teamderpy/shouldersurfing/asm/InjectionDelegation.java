package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
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
		
		if(ShoulderState.doShoulderSurfing() && world != null)
		{
			Vec3d offset = new Vec3d(Config.CLIENT.getOffsetX(), -Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
			double distance = ShoulderSurfingHelper.cameraDistance(world, offset.lengthVector());
			Vec3d scaled = offset.normalize().scale(distance);
			
			ShoulderState.setCameraDistance(distance);
			GlStateManager.translate(scaled.x, scaled.y, scaled.z);
		}
		else
		{
			GlStateManager.translate(x, y, z);
		}
	}
	
	public static int doRenderCrosshair()
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(ShoulderState.isAiming()) ? 0 : 1;
	}
	
	public static RayTraceResult getRayTraceResult(World world, Vec3d vec1, Vec3d vec2)
	{
		return world.rayTraceBlocks(vec1, vec2, false, true, false);
	}
	
	public static RayTraceResult rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Entry<Vec3d, Vec3d> look = ShoulderSurfingHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.world.rayTraceBlocks(look.getKey(), look.getValue(), false, false, true);
		}
		
		Vec3d eyes = entity.getPositionEyes(partialTicks);
		Vec3d look = entity.getLook(partialTicks);
		Vec3d end = eyes.addVector(look.x * blockReachDistance, look.y * blockReachDistance, look.z * blockReachDistance);
		
		return entity.world.rayTraceBlocks(eyes, end, false, false, true);
	}
	
	public static Entry<Vec3d, Vec3d> shoulderSurfingLook(double blockReach)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			return ShoulderSurfingHelper.shoulderSurfingLook(Minecraft.getMinecraft().getRenderViewEntity(), Minecraft.getMinecraft().getRenderPartialTicks(), blockReach * blockReach);
		}
		
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		Vec3d look = renderView.getLook(1.0F);
        Vec3d start = renderView.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
        Vec3d end = start.addVector(look.x * blockReach, look.y * blockReach, look.z * blockReach);
        
        return new SimpleEntry<Vec3d, Vec3d>(start, end);
	}
}
