package com.teamderpy.shouldersurfing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.3
 * @since 2013-01-14
 */
@SideOnly(Side.CLIENT)
public class ShoulderSettings
{
	/**
	 * Whether or not zooming is unlimited
	 */
	public static boolean IS_ZOOM_UNLIMITED = false;
	public static float ZOOM_MINIMUM = 0.3F;
	public static float ZOOM_MAXIMUM = 2.0F;
	
	/**
	 * Whether or not rotation is unlimited
	 */
	public static boolean IS_ROTATION_UNLIMITED = false;
	public static float ROTATION_MINIMUM = -60.0F;
	public static float ROTATION_MAXIMUM = 60.0F;
	
	public static boolean HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = true;
	
	/**
	 * Distance to raytrace to find the player's line of eye sight and whether
	 * or not we use this custom distance. If we are not using the distance
	 * here, then the player's block break length is used.
	 */
	public static boolean USE_CUSTOM_RAYTRACE_DISTANCE = true;
	public static float RAYTRACE_DISTANCE = 400.0F;
	
	/**
	 * If the ray trace hits nothing, assume it hit the horizon
	 */
	public static boolean TRACE_TO_HORIZON_LAST_RESORT = true;
	
	/**
	 * Whether or not the dynamic crosshair is enabled
	 */
	public static boolean IS_DYNAMIC_CROSSHAIR_ENABLED = true;
}
