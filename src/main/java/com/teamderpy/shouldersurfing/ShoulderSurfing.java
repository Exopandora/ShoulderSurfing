package com.teamderpy.shouldersurfing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.proxy.CommonProxy;
import com.teamderpy.shouldersurfing.util.ShoulderState;

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

@SideOnly(Side.CLIENT)
@MCVersion(ShoulderSurfing.MC_VERSION)
@Mod(modid = ShoulderSurfing.MODID, name = ShoulderSurfing.NAME, acceptedMinecraftVersions = "[" + ShoulderSurfing.MC_VERSION + ",)", version = ShoulderSurfing.VERSION, canBeDeactivated = false, guiFactory = "com.teamderpy.shouldersurfing.gui.GuiShoulderSurfingConfigFactory", certificateFingerprint = ShoulderSurfing.CERTIFICATE)
public class ShoulderSurfing
{
	@SidedProxy(clientSide = "com.teamderpy.shouldersurfing.proxy.ClientProxy", serverSide = "com.teamderpy.shouldersurfing.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String NAME = "Shoulder Surfing";
	public static final String MODID = "shouldersurfing";
	public static final String MC_VERSION = "1.9";
	public static final String VERSION = "2.1.1";
	public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
	public static final String CERTIFICATE = "d6261bb645f41db84c74f98e512c2bb43f188af2";
	public static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	public static final float RAYTRACE_DISTANCE = 400.0F;
	public static final ShoulderState STATE = new ShoulderState();
	
	@Instance(MODID)
	private static ShoulderSurfing INSTANCE;
	
	public static EnumShaderCompatibility shaders = EnumShaderCompatibility.NONE;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ShoulderSurfing.proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ShoulderSurfing.proxy.init(event);
	}
	
	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		ShoulderSurfing.proxy.loadComplete(event);
	}
	
	public static float getShadersResmul()
	{
		switch(ShoulderSurfing.shaders)
		{
			case OLD:
				return shadersmod.client.Shaders.shaderPackLoaded ? shadersmod.client.Shaders.configRenderResMul : 1.0F;
			case NEW:
				return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
			default:
				return 1.0F;
		}
	}
	
	public static ShoulderSurfing getInstance()
	{
		return INSTANCE;
	}
}