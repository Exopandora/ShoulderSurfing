package com.teamderpy.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.10
 * @since 2013-11-18
 */

@SideOnly(Side.CLIENT)
@MCVersion(ShoulderSurfing.MC_VERSION)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, acceptedMinecraftVersions = "[" + ShoulderSurfing.MC_VERSION + ",)", version = ShoulderSurfing.VERSION, canBeDeactivated = false, guiFactory = "com.teamderpy.shouldersurfing.gui.GuiShoulderSurfingConfigFactory", certificateFingerprint = ShoulderSurfing.CERTIFICATE)
public class ShoulderSurfing
{
	@SidedProxy(clientSide = "com.teamderpy.shouldersurfing.proxy.ClientProxy", serverSide = "com.teamderpy.shouldersurfing.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	private Configuration config;
	
	public static final String NAME = "Shoulder Surfing";
	public static final String MODID = "shouldersurfing";
	public static final String MC_VERSION = "1.9";
	public static final String VERSION = "1.16";
	public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
	public static final String CERTIFICATE = "d6261bb645f41db84c74f98e512c2bb43f188af2";
	public static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	
	@Instance(MODID)
	private static ShoulderSurfing INSTANCE;
	
	private EnumShaderCompatibility shader = EnumShaderCompatibility.NONE;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		this.syncConfig();
		
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_ZOOM_CAMERA_IN);
		ClientRegistry.registerKeyBinding(ShoulderSettings.KEYBIND_SWAP_SHOULDER);
		
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
		MinecraftForge.EVENT_BUS.register(new ShoulderEventHandler());
	}
	
	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		try
		{
			Class.forName("shadersmod.client.Shaders");
			this.shader = EnumShaderCompatibility.OLD;
		}
		catch(ClassNotFoundException e)
		{
			//Old shader not found
		}
		
		try
		{
			Class.forName("net.optifine.shaders.Shaders");
			this.shader = EnumShaderCompatibility.NEW;
		}
		catch(ClassNotFoundException e)
		{
			//New shader not found
		}
	}
	
	public void syncConfig()
	{
		ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED = this.config.get(Configuration.CATEGORY_GENERAL, "Dynamic Crosshair", ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED, "If enabled, then the crosshair moves around to line up with the block you are facing.").getBoolean(ShoulderSettings.IS_DYNAMIC_CROSSHAIR_ENABLED);
		ShoulderCamera.SHOULDER_ROTATION_YAW = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Rotation Offset", ShoulderCamera.SHOULDER_ROTATION_YAW, "Third person camera rotation").getDouble((double) ShoulderCamera.SHOULDER_ROTATION_YAW);
		ShoulderCamera.SHOULDER_ZOOM_MOD = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Zoom Offset", ShoulderCamera.SHOULDER_ZOOM_MOD, "Third person camera zoom").getDouble((double) ShoulderCamera.SHOULDER_ZOOM_MOD);
		ShoulderSettings.IS_ROTATION_UNLIMITED = this.config.get(Configuration.CATEGORY_GENERAL, "Unlimited Rotation", ShoulderSettings.IS_ROTATION_UNLIMITED, "Whether or not rotation adjustment has limits").getBoolean(ShoulderSettings.IS_ROTATION_UNLIMITED);
		ShoulderSettings.ROTATION_MAXIMUM = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Rotation Maximum", ShoulderSettings.ROTATION_MAXIMUM, "If rotation is limited this is the maximum amount").getDouble((double) ShoulderSettings.ROTATION_MAXIMUM);
		ShoulderSettings.ROTATION_MINIMUM = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Rotation Minimum", ShoulderSettings.ROTATION_MINIMUM, "If rotation is limited this is the minimum amount").getDouble((double) ShoulderSettings.ROTATION_MINIMUM);
		ShoulderSettings.IS_ZOOM_UNLIMITED = this.config.get(Configuration.CATEGORY_GENERAL, "Unlimited Zoom", ShoulderSettings.IS_ZOOM_UNLIMITED, "Whether or not zoom adjustment has limits").getBoolean(ShoulderSettings.IS_ZOOM_UNLIMITED);
		ShoulderSettings.ZOOM_MAXIMUM = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Zoom Maximum", ShoulderSettings.ZOOM_MAXIMUM, "If zoom is limited this is the maximum amount").getDouble((double) ShoulderSettings.ZOOM_MAXIMUM);
		ShoulderSettings.ZOOM_MINIMUM = (float) this.config.get(Configuration.CATEGORY_GENERAL, "Zoom Minimum", ShoulderSettings.ZOOM_MINIMUM, "If zoom is limited this is the minimum amount").getDouble((double) ShoulderSettings.ZOOM_MINIMUM);
		ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT = this.config.get(Configuration.CATEGORY_GENERAL, "Always Show Crosshair", ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT, "Whether or not to show a crosshair in the center of the screen if nothing is in range of you").getBoolean(ShoulderSettings.TRACE_TO_HORIZON_LAST_RESORT);
		ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE = this.config.get(Configuration.CATEGORY_GENERAL, "Show Crosshair Farther", ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE, "Whether or not to show the crosshairs farther than normal").getBoolean(ShoulderSettings.USE_CUSTOM_RAYTRACE_DISTANCE);
		ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA = this.config.get(Configuration.CATEGORY_GENERAL, "Keep Camera Out Of Head", ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA, "Whether or not to hide the player model if the camera gets too close to it").getBoolean(ShoulderSettings.HIDE_PLAYER_IF_TOO_CLOSE_TO_CAMERA);
		ShoulderSettings.ENABLE_3PP_CROSSHAIR = this.config.get(Configuration.CATEGORY_GENERAL, "Third Person Crosshair", ShoulderSettings.ENABLE_3PP_CROSSHAIR, "Enable or disable the crosshair in third person").getBoolean(ShoulderSettings.ENABLE_3PP_CROSSHAIR);
		ShoulderSettings.ENABLE_1PP_CROSSHAIR = this.config.get(Configuration.CATEGORY_GENERAL, "First Person Crosshair", ShoulderSettings.ENABLE_1PP_CROSSHAIR, "Enable or disable the crosshair in first person").getBoolean(ShoulderSettings.ENABLE_1PP_CROSSHAIR);
		ShoulderSettings.ENABLE_ATTACK_INDICATOR = this.config.get(Configuration.CATEGORY_GENERAL, "Third Person Attack Indicator", ShoulderSettings.ENABLE_ATTACK_INDICATOR, "Enable or disable the attack indicator in third person").getBoolean(ShoulderSettings.ENABLE_ATTACK_INDICATOR);
		ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION = this.config.get(Configuration.CATEGORY_GENERAL, "Ignore Blocks Without Collision", ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION, "Whether or not the camera ignores blocks without collision").getBoolean(ShoulderSettings.IGNORE_BLOCKS_WITHOUT_COLLISION);
		ShoulderSettings.REPLACE_DEFAULT_3PP = this.config.get(Configuration.CATEGORY_GENERAL, "Replace Default Perspective", ShoulderSettings.REPLACE_DEFAULT_3PP, "Whether or not to replace the default third person perspective").getBoolean(ShoulderSettings.REPLACE_DEFAULT_3PP);
		ShoulderSettings.DEFAULT_PERSPECTIVE = this.config.get(Configuration.CATEGORY_GENERAL, "Default Perspective", ShoulderSettings.DEFAULT_PERSPECTIVE, "The default perspective when you load the game", new String[] {"First person", "Third person", "Front third person", "Shoulder surfing"}).getString();
		ShoulderSettings.OVERRIDE_MOD_CROSSHAIRS = this.config.get(Configuration.CATEGORY_GENERAL, "Override Mod Crosshairs", ShoulderSettings.OVERRIDE_MOD_CROSSHAIRS, "Whether or not to override mod crosshairs").getBoolean(ShoulderSettings.OVERRIDE_MOD_CROSSHAIRS);
		
		if(this.config.hasChanged())
		{
			this.config.save();
		}
	}
	
	public float getShadersResmul()
	{
		switch(this.shader)
		{
			case OLD:
				return shadersmod.client.Shaders.shaderPackLoaded ? shadersmod.client.Shaders.configRenderResMul : 1.0F;
			case NEW:
				return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
			default:
				return 1.0F;
		}
	}
	
	public Configuration getConfig()
	{
		return this.config;
	}
	
	public static ShoulderSurfing getInstance()
	{
		return INSTANCE;
	}
}