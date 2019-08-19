package com.teamderpy.shouldersurfing.asm;

import java.lang.reflect.Method;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.math.RayTracer;
import com.teamderpy.shouldersurfing.math.VectorConverter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
	/**
	 * Called by injected code to modify the camera rotation yaw
	 */
	public static float getShoulderRotationYaw()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
		{
			return (float) Config.CLIENT.getShoulderRotationYaw();
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera rotation pitch
	 */
	public static float getShoulderRotationPitch()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
		{
			return 0F;
		}
		
		return 0F;
	}
	
	/**
	 * Called by injected code to modify the camera zoom
	 */
	public static float getShoulderZoomMod()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
		{
			return (float) Config.CLIENT.getShoulderZoomMod();
		}
		
		return 1.0F;
	}
	
	/**
	 * Called by injected code to project a raytrace hit to the screen
	 */
	public static void calculateRayTraceProjection()
	{
		if(RayTracer.getRayTraceHit() != null)
		{
			RayTracer.setProjectedVector(VectorConverter.project2D(RayTracer.getRayTraceHit()));
			RayTracer.setRayTraceHit(null);
		}
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
	
	private static double CAMERA_DISTANCE = 0F;
	
	/**
	 * Called by injected code to get the maximum possible distance for the camera
	 */
	public static double calcCameraDistance(double distance, ActiveRenderInfo info)
	{
		double result = distance;
		
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Config.CLIENT.getShoulderSurfing3ppId())
		{
			final float radiant = (float) (Math.PI / 180F);
			final float radiantYaw = info.getYaw() * radiant;
			
			double yawXZlength = MathHelper.sin(InjectionDelegation.getShoulderRotationYaw() * radiant) * distance;
			double yawX = MathHelper.cos(radiantYaw) * yawXZlength;
			double yawZ = MathHelper.sin(radiantYaw) * yawXZlength;
			
			for(int i = 0; i < 8; i++)
			{
				float offsetX = (float)((i & 1) * 2 - 1);
				float offsetY = (float)((i >> 1 & 1) * 2 - 1);
				float offsetZ = (float)((i >> 2 & 1) * 2 - 1);
				
				offsetX = offsetX * 0.1F;
				offsetY = offsetY * 0.1F;
				offsetZ = offsetZ * 0.1F;
				
				Vec3d vec3d = info.getProjectedView().add(offsetX, offsetY, offsetZ);
				Vec3d vec3d1 = new Vec3d(info.getProjectedView().x - info.getLookDirection().x * distance + offsetX + offsetZ + yawX, info.getProjectedView().y - info.getLookDirection().y * distance + offsetY, info.getProjectedView().z - info.getLookDirection().z * distance + offsetZ + yawZ);
				
				RayTraceResult raytraceresult = Minecraft.getInstance().world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, Minecraft.getInstance().renderViewEntity));
				
				if(raytraceresult != null)
				{
					double newDistance = raytraceresult.getHitVec().distanceTo(info.getProjectedView());
					
					if(newDistance < result)
					{
						result = newDistance;
					}
				}
			}
		}
		
		ClientEventHandler.skipRenderPlayer = distance < 0.80;
		return CAMERA_DISTANCE = result;
	}
	
	public static Vec3d getEyePosition(Entity entity, Vec3d positionEyes)
	{
		if(!Config.CLIENT.getCrosshairType().isDynamic())
		{
			final float radiant = (float) (Math.PI / 180F);
			final float radiantPitch = entity.rotationPitch * radiant;
			final float radiantYaw = entity.rotationYaw * radiant;
			
			double pitchYLength = MathHelper.sin(InjectionDelegation.getShoulderRotationPitch() * radiant) * CAMERA_DISTANCE;
			double pitchX = MathHelper.sin(radiantPitch) * MathHelper.sin(-radiantYaw) * pitchYLength;
			double pitchY = MathHelper.cos(radiantPitch) * pitchYLength;
			double pitchZ = MathHelper.sin(radiantPitch) * MathHelper.cos(-radiantYaw) * pitchYLength;
			
			double yawXZlength = MathHelper.sin(InjectionDelegation.getShoulderRotationYaw() * radiant) * CAMERA_DISTANCE;
			double yawX = MathHelper.cos(radiantYaw) * yawXZlength;
			double yawZ = MathHelper.sin(radiantYaw) * yawXZlength;
			
			return positionEyes.add(yawX, 0, yawZ).add(pitchX, pitchY, pitchZ);
		}
		
		return positionEyes;
	}
	
	public static int doRenderCrosshair()
	{
		int skipRender = 0;
		
		if(!Config.CLIENT.getCrosshairVisibility().doRender())
		{
			skipRender = 1;
		}
		else if(Minecraft.getInstance().gameSettings.thirdPersonView > 0 && !Config.CLIENT.show3ppCrosshair())
		{
			skipRender = 1;
		}
		else if(Minecraft.getInstance().gameSettings.thirdPersonView == 0 && !Config.CLIENT.show1ppCrosshair())
		{
			skipRender = 1;
		}
		
		return skipRender;
	}
	
	private static Method METHOD_MOVE_POSITION;
	
	static
	{
		try
		{
			InjectionDelegation.METHOD_MOVE_POSITION = ActiveRenderInfo.class.getDeclaredMethod("func_216782_a", double.class, double.class, double.class);
			InjectionDelegation.METHOD_MOVE_POSITION.setAccessible(true);
		}
		catch(Exception e)
		{
			InjectionDelegation.METHOD_MOVE_POSITION = null;
		}
	}
	
	public static void movePosition(ActiveRenderInfo info, double x, double y, double z) throws Exception
	{
		final float radiant = (float) (Math.PI / 180F);
		final float radiantYaw = InjectionDelegation.getShoulderRotationYaw() * radiant;
		
		double yawX = MathHelper.cos(radiantYaw) * CAMERA_DISTANCE;
		double yawZ = MathHelper.sin(radiantYaw) * CAMERA_DISTANCE;
		
		if(InjectionDelegation.METHOD_MOVE_POSITION != null)
		{
			InjectionDelegation.METHOD_MOVE_POSITION.invoke(info, -yawX, 0, yawZ);
		}
		else
		{
			info.movePosition(-yawX, 0, yawZ);
		}
	}
}
