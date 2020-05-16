package com.teamderpy.shouldersurfing.asm;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Config.ClientConfig.Perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
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
	 * Called by injected code to modify the camera rotation pitch
	 */
	public static float getShoulderRotationPitch()
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId())
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
	
	public static Vec3d getEyePosition(Entity entity, Vec3d positionEyes)
	{
		if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.SHOULDER_SURFING.getPerspectiveId() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			final float radiantPitch = (float) Math.toRadians(entity.rotationPitch);
			final float radiantYaw = (float) Math.toRadians(entity.rotationYaw);
			
			double pitchYLength = MathHelper.sin((float) Math.toRadians(InjectionDelegation.getShoulderRotationPitch())) * InjectionDelegation.cameraDistance;
			double pitchX = MathHelper.sin(radiantPitch) * MathHelper.sin(-radiantYaw) * pitchYLength;
			double pitchY = MathHelper.cos(radiantPitch) * pitchYLength;
			double pitchZ = MathHelper.sin(radiantPitch) * MathHelper.cos(-radiantYaw) * pitchYLength;
			
			double yawXZlength = MathHelper.sin((float) Math.toRadians(InjectionDelegation.getShoulderRotationYaw())) * InjectionDelegation.cameraDistance;
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
		else if(Minecraft.getInstance().gameSettings.thirdPersonView != Perspective.FIRST_PERSON.getPerspectiveId() && !Config.CLIENT.show3ppCrosshair())
		{
			skipRender = 1;
		}
		else if(Minecraft.getInstance().gameSettings.thirdPersonView == Perspective.FIRST_PERSON.getPerspectiveId() && !Config.CLIENT.show1ppCrosshair())
		{
			skipRender = 1;
		}
		
		return skipRender;
	}
}
