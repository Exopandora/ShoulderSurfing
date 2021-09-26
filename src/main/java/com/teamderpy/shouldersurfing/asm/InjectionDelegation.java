package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

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
			Vec3 offset = Vec3.createVectorHelper(Config.CLIENT.getOffsetX(), -Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
			double distance = ShoulderSurfingHelper.cameraDistance(world, offset.lengthVector());
			Vec3 scaled = offset.normalize();
			scaled = Vec3.createVectorHelper(scaled.xCoord * distance, scaled.yCoord * distance, scaled.zCoord * distance);
			
			ShoulderState.setCameraDistance(distance);
			GL11.glTranslated(scaled.xCoord, scaled.yCoord, scaled.zCoord);
		}
		else
		{
			GL11.glTranslated(x, y, z);
		}
	}
	
	public static MovingObjectPosition getRayTraceResult(World world, Vec3 vec1, Vec3 vec2)
	{
		return world.func_147447_a(vec1, vec2, false, true, false);
	}
	
	public static MovingObjectPosition rayTrace(EntityLivingBase entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Entry<Vec3, Vec3> look = ShoulderSurfingHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.worldObj.func_147447_a(look.getKey(), look.getValue(), false, false, true);
		}
		
		Vec3 eyes = entity.getPosition(partialTicks);
		Vec3 look = entity.getLook(partialTicks);
		Vec3 end = eyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
		
		return entity.worldObj.func_147447_a(eyes, end, false, false, true);
	}
	
	public static Entry<Vec3, Vec3> shoulderSurfingLook(double blockReach)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			return ShoulderSurfingHelper.shoulderSurfingLook(Minecraft.getMinecraft().renderViewEntity, Minecraft.getMinecraft().timer.renderPartialTicks, blockReach);
		}
		
		EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
		Vec3 look = renderView.getLook(1.0F);
        Vec3 start = renderView.getPosition(Minecraft.getMinecraft().timer.renderPartialTicks);
        Vec3 end = start.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
        
        return new SimpleEntry<Vec3, Vec3>(start, end);
	}
}
