package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class InjectionDelegation
{
	public static Entry<Vec3d, Vec3d> EntityRenderer_getMouseOver(double blockReach)
	{
		Entity cameraEntity = Minecraft.getMinecraft().getRenderViewEntity();
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(cameraEntity, partialTicks, blockReach * blockReach);
			return new SimpleEntry<Vec3d, Vec3d>(look.cameraPos(), look.traceEndPos());
		}
		
		Vec3d look = cameraEntity.getLook(1.0F);
        Vec3d start = cameraEntity.getPositionEyes(partialTicks);
        Vec3d end = start.add(look.scale(blockReach));
        return new SimpleEntry<Vec3d, Vec3d>(start, end);
	}
	
	public static RayTraceResult Item_rayTrace(World level, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getMinecraft().getRenderViewEntity(), 1.0F, start.squareDistanceTo(end));
			return level.rayTraceBlocks(look.cameraPos(), look.traceEndPos(), stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
		}
		
		return level.rayTraceBlocks(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
	}
	
	public static RayTraceResult EntityRenderer_rayTrace(World world, Vec3d vec1, Vec3d vec2)
	{
		return world.rayTraceBlocks(vec1, vec2, false, true, false);
	}
	
	public static void EntityRenderer_orientCamera(float x, float y, float z, float yaw, float pitch)
	{
		ShoulderRenderer.getInstance().offsetCamera(x, y, z, yaw, pitch);
	}
	
	public static RayTraceResult EntityPlayer_rayTrace(Entity entity, double blockReachDistance, float partialTicks)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(entity, partialTicks, blockReachDistance * blockReachDistance);
			return entity.world.rayTraceBlocks(look.cameraPos(), look.traceEndPos(), false, false, true);
		}
		
		Vec3d look = entity.getLook(partialTicks);
		Vec3d start = entity.getPositionEyes(partialTicks);
		Vec3d end = start.add(look.scale(blockReachDistance));
		return entity.world.rayTraceBlocks(start, end, false, false, true);
	}
	
	public static int GuiIngame_renderAttackIndicator()
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(Minecraft.getMinecraft().objectMouseOver, ShoulderInstance.getInstance().isAiming()) ? 0 : 1;
	}
}
