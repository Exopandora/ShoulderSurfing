package com.teamderpy.shouldersurfing.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config
{
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	private static ModConfig MOD_CONFIG;
	private static CommentedFileConfig CONFIG_DATA;
	
	static
	{
		Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ClientConfig
	{
		private final DoubleValue shoulderRotationYaw;
		private final DoubleValue shoulderZoomMod;
		private final BooleanValue unlimitedRotation;
		private final DoubleValue rotationMin;
		private final DoubleValue rotationMax;
		private final BooleanValue unlimitedZoom;
		private final DoubleValue zoomMin;
		private final DoubleValue zoomMax;
		private final BooleanValue showCrosshairFarther;
		private final BooleanValue keepCameraOutOfHead;
		private final BooleanValue replaceDefaultPerspective;
		private final ConfigValue<Perspective> defaultPerspective;
		private final ConfigValue<CrosshairType> crosshairType;
		private final BooleanValue rememberLastPerspective;
		private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("perspective");
			builder.push("rotation");
			
			this.shoulderRotationYaw = builder
					.comment("Third person camera rotation")
					.translation("Rotation Offset")
					.defineInRange("rotation_offset", 0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.rotationMin = builder
					.comment("If rotation is limited this is the minimum amount")
					.translation("Rotation Minimum")
					.defineInRange("rotation_min", -60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.rotationMax = builder
					.comment("If rotation is limited this is the maximum amount")
					.translation("Rotation Maximum")
					.defineInRange("rotation_max", 60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.unlimitedRotation = builder
					.comment("Whether or not rotation adjustment has limits")
					.translation("Unlimited Rotation")
					.define("unlimited_rotation", false);
			
			builder.pop();
			builder.push("zoom");
			
			this.shoulderZoomMod = builder
					.comment("Third person camera zoom")
					.translation("Zoom Offset")
					.defineInRange("zoom_offset", 0.7, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.zoomMin = builder
					.comment("If zoom is limited this is the minimum amount")
					.translation("Zoom Minimum")
					.defineInRange("zoom_min", 0.3, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.zoomMax = builder
					.comment("If zoom is limited this is the maximum amount")
					.translation("Zoom Maximum")
					.defineInRange("zoom_max", 2.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.unlimitedZoom = builder
					.comment("Whether or not zoom adjustment has limits")
					.translation("Unlimited Zoom")
					.define("unlimited_zoom", false);
			
			builder.pop();
			
			this.keepCameraOutOfHead = builder
					.comment("Whether or not to hide the player model if the camera gets too close to it")
					.translation("Keep Camera Out Of Head")
					.define("keep_camera_out_of_head", true);
			
			this.defaultPerspective = builder
					.comment("The default perspective when you load the game")
					.translation("Default Perspective")
					.defineEnum("default_perspective", Perspective.SHOULDER_SURFING, Perspective.values());
			
			this.rememberLastPerspective = builder
					.comment("Whether or not to remember the last perspective used")
					.translation("Remember Last Perspective")
					.define("remember_last_perspective", true);
			
			this.replaceDefaultPerspective = builder
					.comment("Whether or not to replace the default third person perspective")
					.translation("Replace Default Perspective")
					.define("replace_default_perspective", false);
			
			builder.pop();
			builder.push("crosshair");
			
			this.crosshairType = builder
					.comment("Crosshair type to use for shoulder surfing")
					.translation("Crosshair type")
					.defineEnum("crosshair_type", CrosshairType.ADAPTIVE, CrosshairType.values());
			
			this.showCrosshairFarther = builder
					.comment("Whether or not to show the crosshairs farther than normal")
					.translation("Show Crosshair Farther")
					.define("show_crosshair_farther", true);
			
			builder.push("visibility");
			
			for(Perspective entry : Perspective.values())
			{
				ConfigValue<CrosshairVisibility> crosshairVisibility = builder
						.comment("Crosshair visibility for " + entry.toString().toLowerCase())
						.translation("Crosshair Visibility")
						.defineEnum(entry.toString().toLowerCase(), entry.getDefaultCrosshairVisibility(), CrosshairVisibility.values());
				this.crosshairVisibility.put(entry, crosshairVisibility);
			}
			
			builder.pop();
			builder.pop();
		}
		
		private double getShoulderRotationYaw0()
		{
			return this.shoulderRotationYaw.get();
		}
		
		public void setShoulderRotationYaw(double yaw)
		{
			Config.set(this.shoulderRotationYaw, yaw);
		}
		
		private double getShoulderZoomMod0()
		{
			return this.shoulderZoomMod.get();
		}
		
		public void setShoulderZoomMod(double zoomMod)
		{
			Config.set(this.shoulderZoomMod, zoomMod);
		}
		
		public boolean isRotationUnlimited()
		{
			return this.unlimitedRotation.get();
		}
		
		public void setRotationUnlimited(boolean enabled)
		{
			Config.set(this.unlimitedRotation, enabled);
		}
		
		public double getRotationMin()
		{
			return this.rotationMin.get();
		}
		
		public void setRotationMin(double min)
		{
			Config.set(this.rotationMin, min);
		}
		
		public double getRotationMax()
		{
			return this.rotationMax.get();
		}
		
		public void setRotationMax(double max)
		{
			Config.set(this.rotationMax, max);
		}
		
		public boolean isZoomUnlimited()
		{
			return this.unlimitedZoom.get();
		}
		
		public void setZoomUnlimited(boolean enabled)
		{
			Config.set(this.unlimitedZoom, enabled);
		}
		
		public double getZoomMin()
		{
			return this.zoomMin.get();
		}
		
		public void setZoomMin(double min)
		{
			Config.set(this.zoomMin, min);
		}
		
		public double getZoomMax()
		{
			return this.zoomMax.get();
		}
		
		public void setZoomMax(double max)
		{
			Config.set(this.rotationMax, max);
		}
		
		public CrosshairVisibility getCrosshairVisibility(Perspective perspective)
		{
			return this.crosshairVisibility.get(perspective).get();
		}
		
		public void setCrosshairVisibility(Perspective perspective, CrosshairVisibility visibility)
		{
			Config.set(this.crosshairVisibility.get(perspective), visibility);
		}
		
		public boolean showCrosshairFarther()
		{
			return this.showCrosshairFarther.get();
		}
		
		public void setShowCrosshairFarther(boolean enabled)
		{
			Config.set(this.showCrosshairFarther, enabled);
		}
		
		public boolean keepCameraOutOfHead()
		{
			return this.keepCameraOutOfHead.get();
		}
		
		public void setKeepCameraOutOfHead(boolean enabled)
		{
			Config.set(this.keepCameraOutOfHead, enabled);
		}
		
		public boolean replaceDefaultPerspective()
		{
			return this.replaceDefaultPerspective.get();
		}
		
		public void setReplaceDefaultPerspective(boolean enabled)
		{
			Config.set(this.replaceDefaultPerspective, enabled);
		}
		
		public Perspective getDefaultPerspective()
		{
			return this.defaultPerspective.get();
		}
		
		public void setDefaultPerspective(Perspective perspective)
		{
			Config.set(this.defaultPerspective, perspective);
		}
		
		public CrosshairType getCrosshairType()
		{
			return this.crosshairType.get();
		}
		
		public void setCrosshairType(CrosshairType type)
		{
			Config.set(this.crosshairType, type);
		}
		
		public boolean doRememberLastPerspective()
		{
			return this.rememberLastPerspective.get();
		}
		
		public void setRememberLastPerspective(boolean enabled)
		{
			Config.set(this.rememberLastPerspective, enabled);
		}
		
		public void adjustCameraLeft()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw0() < this.getRotationMax())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw0() + 0.5F);
			}
		}
		
		public void adjustCameraRight()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw0() > this.getRotationMin())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw0() - 0.5F);
			}
		}
		
		public void adjustCameraIn()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod0() < this.getZoomMax())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod0() + 0.01F);
			}
		}
		
		public void adjustCameraOut()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod0() > this.getZoomMin())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod0() - 0.01F);
			}
		}
		
		public void swapShoulder()
		{
			this.setShoulderRotationYaw(-this.getShoulderRotationYaw0());
		}
		
		public float getShoulderRotationYaw()
		{
			if(ClientEventHandler.doShoulderSurfing())
			{
				return (float) this.getShoulderRotationYaw0();
			}
			
			return 0F;
		}
		
		public float getShoulderZoomMod()
		{
			if(ClientEventHandler.doShoulderSurfing())
			{
				return (float) this.getShoulderZoomMod0();
			}
			
			return 1.0F;
		}
	}
	
	protected static <T> void set(ForgeConfigSpec.ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			Config.CONFIG_DATA.set(configValue.getPath(), value);
		}
	}
	
	@SubscribeEvent
	public static void configLoad(ModConfig.Loading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT))
		{
			Config.MOD_CONFIG = event.getConfig();
			Config.CONFIG_DATA = (CommentedFileConfig) Config.MOD_CONFIG.getConfigData();
		}
	}
	
	@SubscribeEvent
	public static void configReload(ModConfig.Reloading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT) && Config.CONFIG_DATA != null)
		{
			Config.CONFIG_DATA.load();
			
			if(Config.CLIENT.doRememberLastPerspective())
			{
				Config.CLIENT.setDefaultPerspective(Perspective.of(Minecraft.getInstance().gameSettings.func_243230_g(), ClientEventHandler.doShoulderSurfing()));
			}
		}
	}
}
