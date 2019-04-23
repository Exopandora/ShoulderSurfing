package com.teamderpy.shouldersurfing;

import org.lwjgl.glfw.GLFW;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.proxy.ClientProxy;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
	private static boolean SHADER_ACTIVE;
	
	public ShoulderSurfing()
	{
		SIDEPROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::commonSetup);
		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC, ShoulderSurfing.MODID + ".toml");
		modEventBus.register(Config.class);
	}
	
	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.registerKeyBinding(KEYBIND_ROTATE_CAMERA_LEFT);
		ClientRegistry.registerKeyBinding(KEYBIND_ROTATE_CAMERA_RIGHT);
		ClientRegistry.registerKeyBinding(KEYBIND_ZOOM_CAMERA_OUT);
		ClientRegistry.registerKeyBinding(KEYBIND_ZOOM_CAMERA_IN);
		ClientRegistry.registerKeyBinding(KEYBIND_SWAP_SHOULDER);
		
		SHADER_ACTIVE = isClassLoaded("net.optifine.shaders.Shaders");
	}
	
	@SubscribeEvent
	public void commonSetup(FMLCommonSetupEvent event)
	{
		SIDEPROXY.setup();
	}
	
	public static float getShadersResMul()
	{
		if(SHADER_ACTIVE)
		{
			return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
		}
		
		return 1.0F;
	}
	
	private static boolean isClassLoaded(String klass)
	{
		try
		{
			Class.forName(klass);
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
		
		return true;
	}
}