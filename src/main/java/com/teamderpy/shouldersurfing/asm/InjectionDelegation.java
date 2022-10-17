package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;

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
	public static Entry<Vec3, Vec3> EntityRenderer_getMouseOver(double blockReach)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getMinecraft().renderViewEntity, Minecraft.getMinecraft().timer.renderPartialTicks, blockReach * blockReach);
			return new SimpleEntry<Vec3, Vec3>(look.cameraPos(), look.traceEndPos());
		}
		
		EntityLivingBase cameraEntity = Minecraft.getMinecraft().renderViewEntity;
		Vec3 look = cameraEntity.getLook(1.0F);
        Vec3 start = cameraEntity.getPosition(Minecraft.getMinecraft().timer.renderPartialTicks);
        Vec3 end = start.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
        return new SimpleEntry<Vec3, Vec3>(start, end);
	}
	
	public static MovingObjectPosition Item_getMovingObjectPositionFromPlayer(World level, Vec3 start, Vec3 end, boolean liquid, boolean b1, boolean b2)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getMinecraft().renderViewEntity, 1.0F, start.squareDistanceTo(end));
			return level.func_147447_a(look.cameraPos(), look.traceEndPos(), liquid, b1, b2);
		}
		
		return level.func_147447_a(start, end, liquid, b1, b2);
	}
	
	public static MovingObjectPosition EntityRenderer_rayTrace(World world, Vec3 vec1, Vec3 vec2)
	{
		return world.func_147447_a(vec1, vec2, false, true, false);
	}
	
	public static void EntityRenderer_orientCamera(float x, float y, float z)
	{
		ShoulderRenderer.getInstance().offsetCamera(x, y, z);
	}
	
	public static MovingObjectPosition EntityPlayer_rayTrace(EntityLivingBase entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.worldObj.func_147447_a(look.cameraPos(), look.traceEndPos(), false, false, true);
		}
		
		Vec3 eyes = entity.getPosition(partialTicks);
		Vec3 look = entity.getLook(partialTicks);
		Vec3 end = eyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
		return entity.worldObj.func_147447_a(eyes, end, false, false, true);
	}
}
