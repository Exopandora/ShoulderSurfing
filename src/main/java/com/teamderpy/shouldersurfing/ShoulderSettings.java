package com.teamderpy.shouldersurfing;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
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
	public static KeyBinding KEYBIND_ROTATE_CAMERA_LEFT = new KeyBinding("Camera left", Keyboard.KEY_LEFT, "key.categories.misc");
	public static KeyBinding KEYBIND_ROTATE_CAMERA_RIGHT = new KeyBinding("Camera right", Keyboard.KEY_RIGHT, "key.categories.misc");
	public static KeyBinding KEYBIND_ZOOM_CAMERA_OUT = new KeyBinding("Camera closer", Keyboard.KEY_UP, "key.categories.misc");
	public static KeyBinding KEYBIND_ZOOM_CAMERA_IN = new KeyBinding("Camera farther", Keyboard.KEY_DOWN, "key.categories.misc");
	public static KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", Keyboard.KEY_O, "key.categories.misc");
	
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
	public static boolean IS_DYNAMIC_CROSSHAIR_ENABLED;
	
	public static boolean ENABLE_CROSSHAIR = true;
	public static boolean ENABLE_ATTACK_INDICATOR = true;
	
	/**
	 * Whether or not the camera distance in third person has to be adjusted when
	 * the ray trace hits a block without collision
	 */
	public static boolean IGNORE_BLOCKS_WITHOUT_COLLISION = true;
	
	public static boolean REPLACE_DEFAULT_3PP = false;
	
	public static String DEFAULT_PERSPECTIVE = "shoulder surfing";
	
	public static int getShoulderSurfing3ppId()
	{
		if(REPLACE_DEFAULT_3PP)
		{
			return 1;
		}
		
		return 3;
	}
}
