package com.teamderpy.shouldersurfing.asm;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2013-01-14
 * 
 *		Injected code is delegated here
 */
@OnlyIn(Dist.CLIENT)
public final class InjectionDelegation
{
	public static double cameraDistance = 0F;
	
	/**
	 * Called by injected code to modify the camera rotation yaw
	 */
	public static float getShoulderRotationYaw()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
		{
			return (float) Config.CLIENT.getShoulderRotationYaw();
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera zoom
	 */
	public static float getShoulderZoomMod()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
		{
			return (float) Config.CLIENT.getShoulderZoomMod();
		}
		
		return 1.0F;
	}
	
	/**
	 * Called by injected code to get the maximum value for third person
	 */
	public static int getMax3ppId()
	{
		if(Config.CLIENT.replaceDefaultPerspective())
		{
			return 2;
		}
		
		return 3;
	}
	
	public static RayTraceResult pick(Entity entity, RayTraceResult result, double distance, float partialTicks, boolean stopOnFluid)
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			final float yaw = (float) Math.toRadians(entity.rotationYaw);
			
			double yawXZlength = MathHelper.sin((float) Math.toRadians(InjectionDelegation.getShoulderRotationYaw())) * InjectionDelegation.cameraDistance;
			double yawX = MathHelper.cos(yaw) * yawXZlength;
			double yawZ = MathHelper.sin(yaw) * yawXZlength;
			
			Vector3d start = entity.getEyePosition(partialTicks).add(yawX, 0, yawZ);
			Vector3d look = entity.getLook(partialTicks);
			Vector3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
			
			return entity.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, entity));
		}
		
		return result;
	}
	
	public static int doRenderCrosshair()
	{
		int skipRender = 0;
		Perspective perspective = Perspective.of(Minecraft.getInstance().gameSettings.thirdPersonView);
		
		if(!Config.CLIENT.getCrosshairVisibility(perspective).doRender())
		{
			skipRender = 1;
		}
		
		return skipRender;
	}
}
