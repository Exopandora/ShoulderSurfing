package com.teamderpy.shouldersurfing;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.proxy.ClientProxy;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShoulderSurfing.MODID)
public class ShoulderSurfing
{
	public static final String MODID = "shouldersurfing";
	
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, "key.categories.misc");
	public static final KeyBinding KEYBIND_ROTATE_CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, "key.categories.misc");
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_OUT = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, "key.categories.misc");
	public static final KeyBinding KEYBIND_ZOOM_CAMERA_IN = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, "key.categories.misc");
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, "key.categories.misc");
	
	public static final float RAYTRACE_DISTANCE = 400.0F;
	
	private static CommonProxy SIDEPROXY;
	private static boolean SHADER_INACTIVE;
	
	public ShoulderSurfing()
	{
		SIDEPROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::configLoad);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::configReload);
		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
	}
	
	public void clientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.registerKeyBinding(KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KEYBIND_ZOOM_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KEYBIND_SWAP_SHOULDER);
		
		if(Config.CLIENT.getDefaultPerspective().equalsIgnoreCase("first person"))
		{
			Minecraft.getInstance().gameSettings.thirdPersonView = 0;
		}
		else if(Config.CLIENT.getDefaultPerspective().equalsIgnoreCase("third person"))
		{
			Minecraft.getInstance().gameSettings.thirdPersonView = 1;
		}
		else if(Config.CLIENT.getDefaultPerspective().equalsIgnoreCase("front third person"))
		{
			Minecraft.getInstance().gameSettings.thirdPersonView = 2;
		}
		else if(Config.CLIENT.getDefaultPerspective().equalsIgnoreCase("shoulder surfing"))
		{
			Minecraft.getInstance().gameSettings.thirdPersonView = Config.CLIENT.getShoulderSurfing3ppId();
		}
	}
	
	public void commonSetup(FMLCommonSetupEvent event)
	{
		SIDEPROXY.setup();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		try
		{
			Class.forName("net.optifine.shaders.Shaders");
		}
		catch(ClassNotFoundException e)
		{
			SHADER_INACTIVE = true;
		}
	}
	
	public static float getShadersResmul()
	{
		if(SHADER_INACTIVE)
		{
			return 1.0F;
		}
		
		return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
	}
}