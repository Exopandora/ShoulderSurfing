package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class InjectionDelegation
{
	public static Entry<Vec3, Vec3> EntityRenderer_getMouseOver(double blockReach)
	{
		Entity cameraEntity = Minecraft.getMinecraft().getRenderViewEntity();
		float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(cameraEntity, partialTicks, blockReach * blockReach);
			Vec3 eyePosition = cameraEntity.getPositionEyes(partialTicks);
			Vec3 from = eyePosition.add(look.headOffset());
			return new SimpleEntry<Vec3, Vec3>(from, look.traceEndPos());
		}
		
		Vec3 look = cameraEntity.getLook(1.0F);
        Vec3 start = cameraEntity.getPositionEyes(partialTicks);
        Vec3 end = start.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
        return new SimpleEntry<Vec3, Vec3>(start, end);
	}
	
	public static MovingObjectPosition Item_getMovingObjectPositionFromPlayer(World level, Vec3 start, Vec3 end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(entity, 1.0F, start.squareDistanceTo(end));
			Vec3 eyePosition = entity.getPositionEyes(Minecraft.getMinecraft().timer.renderPartialTicks);
			Vec3 from = eyePosition.add(look.headOffset());
			return level.rayTraceBlocks(from, look.traceEndPos(), stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
		}
		
		return level.rayTraceBlocks(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
	}
	
	public static MovingObjectPosition EntityRenderer_rayTrace(World world, Vec3 vec1, Vec3 vec2)
	{
		return world.rayTraceBlocks(vec1, vec2, false, true, false);
	}
	
	public static void EntityRenderer_orientCamera(float x, float y, float z)
	{
		ShoulderRenderer.getInstance().offsetCamera(x, y, z);
	}
	
	public static MovingObjectPosition EntityPlayer_rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			return ShoulderHelper.traceBlocks(entity, false, blockReachDistance, partialTicks, true);
		}
		
		Vec3 look = entity.getLook(partialTicks);
		Vec3 start = entity.getPositionEyes(partialTicks);
		Vec3 end = start.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
		return entity.worldObj.rayTraceBlocks(start, end, false, false, true);
	}
}
