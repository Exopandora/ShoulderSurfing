package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class InjectionDelegation
{
	// XXX Forge Hooks
	// public static MovingObjectPosition rayTraceEyes(EntityLivingBase entity, double length)
	// public static Vec3 rayTraceEyeHitVec(EntityLivingBase entity, double length)
	
	public static void cameraSetup(float x, float y, float z)
	{
		final World world = Minecraft.getMinecraft().theWorld;
		
		if(ShoulderState.doShoulderSurfing() && world != null)
		{
			Vec3 offset = new Vec3(Config.CLIENT.getOffsetX(), -Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
			double distance = ShoulderSurfingHelper.cameraDistance(world, offset.lengthVector());
			Vec3 scaled = offset.normalize();
			scaled = new Vec3(scaled.xCoord * distance, scaled.yCoord * distance, scaled.zCoord * distance);
			
			ShoulderState.setCameraDistance(distance);
			GlStateManager.translate(scaled.xCoord, scaled.yCoord, scaled.zCoord);
		}
		else
		{
			GlStateManager.translate(x, y, z);
		}
	}
	
	public static MovingObjectPosition getRayTraceResult(World world, Vec3 vec1, Vec3 vec2)
	{
		return world.rayTraceBlocks(vec1, vec2, false, true, false);
	}
	
	public static MovingObjectPosition rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Entry<Vec3, Vec3> look = ShoulderSurfingHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.worldObj.rayTraceBlocks(look.getKey(), look.getValue(), false, false, true);
		}
		
		Vec3 eyes = entity.getPositionEyes(partialTicks);
		Vec3 look = entity.getLook(partialTicks);
		Vec3 end = eyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
		
		return entity.worldObj.rayTraceBlocks(eyes, end, false, false, true);
	}
	
	public static Entry<Vec3, Vec3> shoulderSurfingLook(double blockReach)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			return ShoulderSurfingHelper.shoulderSurfingLook(Minecraft.getMinecraft().getRenderViewEntity(), Minecraft.getMinecraft().timer.renderPartialTicks, blockReach);
		}
		
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		Vec3 look = renderView.getLook(1.0F);
        Vec3 start = renderView.getPositionEyes(Minecraft.getMinecraft().timer.renderPartialTicks);
        Vec3 end = start.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
        
        return new SimpleEntry<Vec3, Vec3>(start, end);
	}
}
