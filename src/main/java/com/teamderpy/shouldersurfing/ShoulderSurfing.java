package com.teamderpy.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamderpy.shouldersurfing.asm.ShoulderTransformations;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.10
 * @since 2013-11-18
 */

@SideOnly(Side.CLIENT)
@MCVersion(ShoulderSurfing.MC_VERSION)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, acceptedMinecraftVersions = "[" + ShoulderSurfing.MC_VERSION + ",)", version = ShoulderSurfing.VERSION, canBeDeactivated = false, guiFactory = "com.teamderpy.shouldersurfing.gui.GuiShoulderSurfingConfigFactory")
public class ShoulderSurfing
{
	@Instance(MODID)
	public ShoulderSurfing INSTACE;
	
	@SidedProxy(clientSide = "com.teamderpy.shouldersurfing.proxy.ClientProxy", serverSide = "com.teamderpy.shouldersurfing.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static Configuration CONFIG;
	
	public static final String NAME = "Shoulder Surfing";
	public static final String MODID = "shouldersurfing";
	public static final String MC_VERSION = "1.7.10";
	public static final String VERSION = "1.9";
	public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
	public static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
		CONFIG.load();
		
		syncConfig();
		
		if(ShoulderSettings.DEFAULT_PERSPECTIVE.equals("first person"))
		{
			Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
		}
		else if(ShoulderSettings.DEFAULT_PERSPECTIVE.equals("third person"))
		{
			Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
		}
		else if(ShoulderSettings.DEFAULT_PERSPECTIVE.equals("front third person"))
		{
			Minecraft.getMinecraft().gameSettings.thirdPersonView = 2;
		}
		else if(ShoulderSettings.DEFAULT_PERSPECTIVE.equals("shoulder surfing"))
		{
			Minecraft.getMinecraft().gameSettings.thirdPersonView = ShoulderSettings.getShoulderSurfing3ppId();
		}
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_SWAP_SHOULDER);
		
		MinecraftForge.EVENT_BUS.register(new ShoulderEventHandler());
		FMLCommonHandler.instance().bus().register(new ShoulderEventHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if(ShoulderTransformations.MODIFICATIONS != ShoulderTransformations.TOTAL_MODIFICATIONS)
		{
			ShoulderSurfing.LOGGER.error("Only found " + ShoulderTransformations.MODIFICATIONS + " code injections, but expected " + ShoulderTransformations.TOTAL_MODIFICATIONS);
			ShoulderSurfing.LOGGER.error("ShoulderSurfing should be disabled!");
		}
		else
		{
			ShoulderSurfing.LOGGER.info("Loaded " + ShoulderTransformations.MODIFICATIONS + " code injections, ShoulderSurfing good to go!");
		}
	}
	
	public static void syncConfig()
	{
		ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED = CONFIG.get(Configuration.CATEGORY_GENERAL, "Dynamic Crosshair", ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED, "If enabled, then the crosshair moves around to line up with the block you are facing.").getBoolean(ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED);
		ShoulderCamera.SHOULDER_ROTATION = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION, "Third person camera rotation").getDouble((double) ShoulderCamera.SHOULDER_ROTATION);
		ShoulderCamera.SHOULDER_ZOOM_MOD = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").getDouble((double) ShoulderCamera.SHOULDER_ZOOM_MOD);
		ShoulderSettings.IS_ROTATION_UNLIMITED = CONFIG.get(Configuration.CATEGORY_GENERAL, "Unlimited Rotation", ShoulderSettings.IS_ROTATION_UNLIMITED, "Whether or not rotation adjustment has limits").getBoolean(ShoulderSettings.IS_ROTATION_UNLIMITED);
		ShoulderSettings.ROTATION_MAXIMUM = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Rotation Maximum", ShoulderSettings.ROTATION_MAXIMUM, "If rotation is limited this is the maximum amount").getDouble((double) ShoulderSettings.ROTATION_MAXIMUM);
		ShoulderSettings.ROTATION_MINIMUM = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Rotation Minimum", ShoulderSettings.ROTATION_MINIMUM, "If rotation is limited this is the minimum amount").getDouble((double) ShoulderSettings.ROTATION_MINIMUM);
		ShoulderSettings.IS_ZOOM_UNLIMITED = CONFIG.get(Configuration.CATEGORY_GENERAL, "Unlimited Zoom", ShoulderSettings.IS_ZOOM_UNLIMITED, "Whether or not zoom adjustment has limits").getBoolean(ShoulderSettings.IS_ZOOM_UNLIMITED);
		ShoulderSettings.ZOOM_MAXIMUM = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Zoom Maximum", ShoulderSettings.ZOOM_MAXIMUM, "If zoom is limited this is the maximum amount").getDouble((double) ShoulderSettings.ZOOM_MAXIMUM);
		ShoulderSettings.ZOOM_MINIMUM = (float) CONFIG.get(Configuration.CATEGORY_GENERAL, "Zoom Minimum", ShoulderSettings.ZOOM_MINIMUM, "If zoom is limited this is the minimum amount").getDouble((double) ShoulderSettings.ZOOM_MINIMUM);
		ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT = CONFIG.get(Configuration.CATEGORY_GENERAL, "Always Show Crosshair", ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT, "Whether or not to show a crosshair in the center of the screen if nothing is in range of you").getBoolean(ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT);
		ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE = CONFIG.get(Configuration.CATEGORY_GENERAL, "Show Crosshair Farther", ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE, "Whether or not to show the crosshairs farther than normal").getBoolean(ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE);
		ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = CONFIG.get(Configuration.CATEGORY_GENERAL, "Keep Camera Out Of Head", ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA, "Whether or not to hide the player model if the camera gets too close to it").getBoolean(ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA);
		ShoulderSettings.ENABLE_CROSSHAIR = CONFIG.get(Configuration.CATEGORY_GENERAL, "Third Person Crosshair", ShoulderSettings.ENABLE_CROSSHAIR, "Enable or disable the crosshair in third person").getBoolean(ShoulderSettings.ENABLE_CROSSHAIR);
		ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION = CONFIG.get(Configuration.CATEGORY_GENERAL, "Ignore Blocks Without Collision", ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION, "Whether or not the camera ignores blocks without collision").getBoolean(ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION);
		ShoulderSettings.REPLACE_DEFAULT_3PP = CONFIG.get(Configuration.CATEGORY_GENERAL, "Replace Default Perspective", ShoulderSettings.REPLACE_DEFAULT_3PP, "Whether or not to replace the default third person perspective").getBoolean(ShoulderSettings.REPLACE_DEFAULT_3PP);
		ShoulderSettings.DEFAULT_PERSPECTIVE = CONFIG.get(Configuration.CATEGORY_GENERAL, "Default Perspective", ShoulderSettings.DEFAULT_PERSPECTIVE, "The default perspective when you load the game", new String[] {"First person", "Third person", "Front third person", "Shoulder surfing"}).getString();
		
		if(ShoulderSurfing.CONFIG.hasChanged())
		{
			ShoulderSurfing.CONFIG.save();
		}
	}
}