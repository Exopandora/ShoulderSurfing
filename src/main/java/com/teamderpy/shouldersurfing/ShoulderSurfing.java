package com.teamderpy.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.teamderpy.shouldersurfing.asm.ShoulderTransformations;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.10
 * @since 2013-11-18
 */

@SideOnly(Side.CLIENT)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, acceptedMinecraftVersions = "[" + ShoulderSurfing.MC_VERSION + ",)", version = ShoulderSurfing.VERSION, canBeDeactivated = false, guiFactory = "com.teamderpy.shouldersurfing.gui.GuiShoulderSurfingConfigFactory")
public class ShoulderSurfing
{
	@Instance(MODID)
	public ShoulderSurfing INSTACE;
	
	@SidedProxy(clientSide = "com.teamderpy.shouldersurfing.proxy.ClientProxy", serverSide = "com.teamderpy.shouldersurfing.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static Configuration config;
	
	public static final String NAME = "Shoulder Surfing";
	public static final String MODID = "shouldersurfing";
	public static final String MC_VERSION = "1.10";
	public static final String VERSION = "1.2";
	public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
	
	public static KeyBinding KEYBIND_ROTATE_CAMERA_LEFT = new KeyBinding("Camera adj left", Keyboard.KEY_J, "key.categories.misc");
	public static KeyBinding KEYBIND_ROTATE_CAMERA_RIGHT = new KeyBinding("Camera adj right", Keyboard.KEY_L, "key.categories.misc");
	public static KeyBinding KEYBIND_ZOOM_CAMERA_OUT = new KeyBinding("Camera adj closer", Keyboard.KEY_I, "key.categories.misc");
	public static KeyBinding KEYBIND_ZOOM_CAMERA_IN = new KeyBinding("Camera adj farther", Keyboard.KEY_K, "key.categories.misc");
	
	public static Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		
		this.syncConfig();
		
		ClientRegistry.registerKeyBinding(this.KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(this.KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(this.KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(this.KEYBIND_ZOOM_CAMERA_IN);
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ShoulderEventHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		int expMods = ShoulderTransformations.CODE_MODIFICATIONS;
		
		if(ShoulderTransformations.modifications != expMods)
		{
			this.LOGGER.error("Only found " + ShoulderTransformations.modifications + " code injections, but expected " + expMods);
			this.LOGGER.error("ShoulderSurfing should be disabled!");
		}
		else
		{
			this.LOGGER.info("Loaded " + ShoulderTransformations.modifications + " code injections, ShoulderSurfing good to go!");
		}
	}
	
	public static void syncConfig()
	{
		ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED = config.get(Configuration.CATEGORY_GENERAL, "Dynamic Crosshair", ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED, "If enabled, then the crosshair moves around to line up with the block you are facing.").getBoolean(ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED);
		ShoulderCamera.SHOULDER_ROTATION = (float) config.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION, "Third person camera rotation").getDouble((double) ShoulderCamera.SHOULDER_ROTATION);
		ShoulderCamera.SHOULDER_ZOOM_MOD = (float) config.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").getDouble((double) ShoulderCamera.SHOULDER_ZOOM_MOD);
		ShoulderSettings.IS_ROTATION_UNLIMITED = config.get(Configuration.CATEGORY_GENERAL, "Unlimited Rotation", ShoulderSettings.IS_ROTATION_UNLIMITED, "Whether or not rotation adjustment has limits").getBoolean(ShoulderSettings.IS_ROTATION_UNLIMITED);
		ShoulderSettings.ROTATION_MAXIMUM = (float) config.get(Configuration.CATEGORY_GENERAL, "Rotation Maximum", ShoulderSettings.ROTATION_MAXIMUM, "If rotation is limited this is the maximum amount").getDouble((double) ShoulderSettings.ROTATION_MAXIMUM);
		ShoulderSettings.ROTATION_MINIMUM = (float) config.get(Configuration.CATEGORY_GENERAL, "Rotation Minimum", ShoulderSettings.ROTATION_MINIMUM, "If rotation is limited this is the minimum amount").getDouble((double) ShoulderSettings.ROTATION_MINIMUM);
		ShoulderSettings.IS_ZOOM_UNLIMITED = config.get(Configuration.CATEGORY_GENERAL, "Unlimited Zoom", ShoulderSettings.IS_ZOOM_UNLIMITED, "Whether or not zoom adjustment has limits").getBoolean(ShoulderSettings.IS_ZOOM_UNLIMITED);
		ShoulderSettings.ZOOM_MAXIMUM = (float) config.get(Configuration.CATEGORY_GENERAL, "Zoom Maximum", ShoulderSettings.ZOOM_MAXIMUM, "If zoom is limited this is the maximum amount").getDouble((double) ShoulderSettings.ZOOM_MAXIMUM);
		ShoulderSettings.ZOOM_MINIMUM = (float) config.get(Configuration.CATEGORY_GENERAL, "Zoom Minimum", ShoulderSettings.ZOOM_MINIMUM, "If zoom is limited this is the minimum amount").getDouble((double) ShoulderSettings.ZOOM_MINIMUM);
		ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT = config.get(Configuration.CATEGORY_GENERAL, "Always Show Crosshair", ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT, "Whether or not to show a crosshair in the center of the screen if nothing is in range of you").getBoolean(ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT);
		ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE = config.get(Configuration.CATEGORY_GENERAL, "Show Crosshair Farther", ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE, "Whether or not to show the crosshairs farther than normal").getBoolean(ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE);
		ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = config.get(Configuration.CATEGORY_GENERAL, "Keep Camera Out Of Head", ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA, "Whether or not to hide the player model if the camera gets too close to it").getBoolean(ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA);
		
		if(config.hasChanged())
		{
			config.save();
		}
	}
}
