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
		EntityLivingBase cameraEntity = Minecraft.getMinecraft().renderViewEntity;
		float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(cameraEntity, partialTicks, blockReach * blockReach);
			Vec3 eyePosition = cameraEntity.getPosition(partialTicks);
			Vec3 from = eyePosition.addVector(look.headOffset().xCoord, look.headOffset().yCoord, look.headOffset().zCoord);
			return new SimpleEntry<Vec3, Vec3>(from, look.traceEndPos());
		}
		
		Vec3 look = cameraEntity.getLook(1.0F);
        Vec3 start = cameraEntity.getPosition(partialTicks);
        Vec3 end = start.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
        return new SimpleEntry<Vec3, Vec3>(start, end);
	}
	
	public static MovingObjectPosition Item_getMovingObjectPositionFromPlayer(World level, Vec3 start, Vec3 end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getMinecraft().renderViewEntity, 1.0F, start.squareDistanceTo(end));
			EntityLivingBase entity = Minecraft.getMinecraft().thePlayer;
			Vec3 eyePosition = entity.getPosition(Minecraft.getMinecraft().timer.renderPartialTicks);
			Vec3 from = eyePosition.addVector(look.headOffset().xCoord, look.headOffset().yCoord, look.headOffset().zCoord);
			return level.func_147447_a(from, look.traceEndPos(), stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
		}
		
		return level.func_147447_a(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
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
			return ShoulderHelper.traceBlocks(entity, false, blockReachDistance, partialTicks, true);
		}
		
		Vec3 look = entity.getLook(partialTicks);
		Vec3 start = entity.getPosition(partialTicks);
		Vec3 end = start.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
		return entity.worldObj.func_147447_a(start, end, false, false, true);
	}
}
