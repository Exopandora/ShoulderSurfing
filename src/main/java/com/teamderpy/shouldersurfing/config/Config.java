package com.teamderpy.shouldersurfing.config;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
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
	
	public static class ClientConfig
	{
		private boolean dynamicCrosshair;
		private double shoulderRotationYaw;
		private double shoulderZoomMod;
		private boolean unlimitedRotation;
		private double rotationMin;
		private double rotationMax;
		private boolean unlimitedZoom;
		private double zoomMin;
		private double zoomMax;
		private boolean show3ppCrosshair;
		private boolean show1ppCrosshair;
		private boolean alwaysShowCrosshair;
		private boolean showCrosshairFarther;
		private boolean keepCameraOutOfHead;
		private boolean attackIndicator;
		private boolean ignoreBlocksWithoutCollision;
		private boolean replaceDefaultPerspective;
		private String defaultPerspective;
		
		private final BooleanValue valueDynamicCrosshair;
		private final DoubleValue valueShoulderRotationYaw;
		private final DoubleValue valueShoulderZoomMod;
		private final BooleanValue valueUnlimitedRotation;
		private final DoubleValue valueRotationMin;
		private final DoubleValue valueRotationMax;
		private final BooleanValue valueUnlimitedZoom;
		private final DoubleValue valueZoomMin;
		private final DoubleValue valueZoomMax;
		private final BooleanValue valueShow3ppCrosshair;
		private final BooleanValue valueShow1ppCrosshair;
		private final BooleanValue valueAlwaysShowCrosshair;
		private final BooleanValue valueShowCrosshairFarther;
		private final BooleanValue valueKeepCameraOutOfHead;
		private final BooleanValue valueAttackIndicator;
		private final BooleanValue valueIgnoreBlocksWithoutCollision;
		private final BooleanValue valueReplaceDefaultPerspective;
		private final ConfigValue<String> valueDefaultPerspective;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			this.valueDynamicCrosshair = builder
					.comment("If enabled, then the crosshair moves around to line up with the block you are facing.")
					.translation("Dynamic Crosshair")
					.define("dynamic_crosshair", false);
			
			this.valueShoulderRotationYaw = builder
					.comment("Third person camera rotation")
					.translation("Rotation Offset")
					.defineInRange("rotation_offset", 0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.valueShoulderZoomMod = builder
					.comment("Third person camera zoom")
					.translation("Zoom Offset")
					.defineInRange("zoom_offset", 0.7, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.valueUnlimitedRotation = builder
					.comment("Whether or not rotation adjustment has limits")
					.translation("Unlimited Rotation")
					.define("unlimited_rotation", false);
			
			this.valueRotationMin = builder
					.comment("If rotation is limited this is the minimum amount")
					.translation("Rotation Minimum")
					.defineInRange("rotation_min", -60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.valueRotationMax = builder
					.comment("If rotation is limited this is the maximum amount")
					.translation("Rotation Maximum")
					.defineInRange("rotation_max", 60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.valueUnlimitedZoom = builder
					.comment("Whether or not zoom adjustment has limits")
					.translation("Unlimited Zoom")
					.define("unlimited_zoom", false);
			
			this.valueZoomMin = builder
					.comment("If zoom is limited this is the minimum amount")
					.translation("Zoom Minimum")
					.defineInRange("zoom_min", 0.3, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.valueZoomMax = builder
					.comment("If zoom is limited this is the maximum amount")
					.translation("Zoom Maximum")
					.defineInRange("zoom_max", 2.0, -Double.MAX_VALUE, Double.MAX_VALUE);
						
			this.valueShow3ppCrosshair = builder
					.comment("Enable or disable the crosshair in third person")
					.translation("Third Person Crosshair")
					.define("3pp_crosshair", true);
			
			this.valueShow1ppCrosshair = builder
					.comment("Enable or disable the crosshair in first person")
					.translation("First Person Crosshair")
					.define("1pp_crosshair", true);
			
			this.valueAlwaysShowCrosshair = builder
					.comment("Whether or not to show a crosshair in the center of the screen if nothing is in range of you")
					.translation("Always Show Crosshair")
					.define("always_show_crosshair", true);
			
			this.valueShowCrosshairFarther = builder
					.comment("Whether or not to show the crosshairs farther than normal")
					.translation("Show Crosshair Farther")
					.define("show_crosshair_farther", true);
			
			this.valueKeepCameraOutOfHead = builder
					.comment("Whether or not to hide the player model if the camera gets too close to it")
					.translation("Keep Camera Out Of Head")
					.define("keep_camera_out_of_head", true);
			
			this.valueAttackIndicator = builder
					.comment("Enable or disable the attack indicator in third person")
					.translation("Third Person Attack Indicator")
					.define("third_person_attack_indicator", true);
			
			this.valueIgnoreBlocksWithoutCollision = builder
					.comment("Whether or not the camera ignores blocks without collision")
					.translation("Ignore Blocks Without Collision")
					.define("third_person_attack_indicator", true);
			
			this.valueReplaceDefaultPerspective = builder
					.comment("Whether or not to replace the default third person perspective")
					.translation("Replace Default Perspective")
					.define("replace_default_perspective", false);
			
			List<String> perspectives = Lists.newArrayList("first person", "third person", "front third person", "shoulder surfing");
			
			this.valueDefaultPerspective = builder
					.comment("The default perspective when you load the game")
					.translation("Default Perspective")
					.defineInList("default_perspective", perspectives.get(perspectives.size() - 1), perspectives);
		}
		
		public void read()
		{
			this.dynamicCrosshair = this.valueDynamicCrosshair.get();
			this.shoulderRotationYaw = this.valueShoulderRotationYaw.get();
			this.shoulderZoomMod = this.valueShoulderZoomMod.get();
			this.unlimitedRotation = this.valueUnlimitedRotation.get();
			this.rotationMin = this.valueRotationMin.get();
			this.rotationMax = this.valueRotationMax.get();
			this.unlimitedZoom = this.valueUnlimitedZoom.get();
			this.zoomMin = this.valueZoomMin.get();
			this.zoomMax = this.valueZoomMax.get();
			this.show3ppCrosshair = this.valueShow3ppCrosshair.get();
			this.show1ppCrosshair = this.valueShow1ppCrosshair.get();
			this.alwaysShowCrosshair = this.valueAlwaysShowCrosshair.get();
			this.showCrosshairFarther = this.valueShowCrosshairFarther.get();
			this.keepCameraOutOfHead = this.valueKeepCameraOutOfHead.get();
			this.attackIndicator = this.valueAttackIndicator.get();
			this.ignoreBlocksWithoutCollision = this.valueIgnoreBlocksWithoutCollision.get();
			this.replaceDefaultPerspective = this.valueReplaceDefaultPerspective.get();
			this.defaultPerspective = this.valueDefaultPerspective.get();
		}
		
		private void write()
		{
			Config.set(this.valueDynamicCrosshair, this.dynamicCrosshair);
			Config.set(this.valueShoulderRotationYaw, this.shoulderRotationYaw);
			Config.set(this.valueShoulderZoomMod, this.shoulderZoomMod);
			Config.set(this.valueUnlimitedRotation, this.unlimitedRotation);
			Config.set(this.valueRotationMin, this.rotationMin);
			Config.set(this.valueRotationMax, this.rotationMax);
			Config.set(this.valueUnlimitedZoom, this.unlimitedZoom);
			Config.set(this.valueZoomMin, this.zoomMin);
			Config.set(this.valueZoomMax, this.zoomMax);
			Config.set(this.valueShow3ppCrosshair, this.show3ppCrosshair);
			Config.set(this.valueShow1ppCrosshair, this.show1ppCrosshair);
			Config.set(this.valueAlwaysShowCrosshair, this.alwaysShowCrosshair);
			Config.set(this.valueShowCrosshairFarther, this.showCrosshairFarther);
			Config.set(this.valueKeepCameraOutOfHead, this.keepCameraOutOfHead);
			Config.set(this.valueAttackIndicator, this.attackIndicator);
			Config.set(this.valueIgnoreBlocksWithoutCollision, this.ignoreBlocksWithoutCollision);
			Config.set(this.valueReplaceDefaultPerspective, this.replaceDefaultPerspective);
			Config.set(this.valueDefaultPerspective, this.defaultPerspective);
		}
		
		public boolean dynamicCrosshair()
		{
			return this.dynamicCrosshair;
		}
		
		public void setDynamicCrosshair(boolean enabled)
		{
			this.dynamicCrosshair = enabled;
			this.write();
		}
		
		public double getShoulderRotationYaw()
		{
			return this.shoulderRotationYaw;
		}
		
		public void setShoulderRotationYaw(double yaw)
		{
			this.shoulderRotationYaw = yaw;
			this.write();
		}
		
		public double getShoulderZoomMod()
		{
			return this.shoulderZoomMod;
		}
		
		public void setShoulderZoomMod(double zoomMod)
		{
			this.shoulderZoomMod = zoomMod;
			this.write();
		}
		
		public boolean isRotationUnlimited()
		{
			return this.unlimitedRotation;
		}
		
		public void setRotationUnlimited(boolean enabled)
		{
			this.unlimitedRotation = enabled;
			this.write();
		}
		
		public double getRotationMin()
		{
			return this.rotationMin;
		}
		
		public void setRotationMin(double min)
		{
			this.rotationMin = min;
			this.write();
		}
		
		public double getRotationMax()
		{
			return this.rotationMax;
		}
		
		public void setRotationMax(double max)
		{
			this.rotationMax = max;
			this.write();
		}
		
		public boolean isZoomUnlimited()
		{
			return this.unlimitedZoom;
		}
		
		public void setZoomUnlimited(boolean enabled)
		{
			this.unlimitedZoom = enabled;
			this.write();
		}
		
		public double getZoomMin()
		{
			return this.zoomMin;
		}
		
		public void setZoomMin(double min)
		{
			this.zoomMin = min;
			this.write();
		}
		
		public double getZoomMax()
		{
			return this.zoomMax;
		}
		
		public void setZoomMax(double max)
		{
			this.rotationMax = max;
			this.write();
		}
		
		public boolean show3ppCrosshair()
		{
			return this.show3ppCrosshair;
		}
		
		public void setShow3ppCrosshair(boolean enabled)
		{
			this.show3ppCrosshair = enabled;
			this.write();
		}
		
		public boolean show1ppCrosshair()
		{
			return this.show1ppCrosshair;
		}
		
		public void setShow1ppCrosshair(boolean enabled)
		{
			this.show1ppCrosshair = enabled;
			this.write();
		}
		
		public boolean alwaysShowCrosshair()
		{
			return this.alwaysShowCrosshair;
		}
		
		public void setAlwaysShowCrosshair(boolean enabled)
		{
			this.alwaysShowCrosshair = enabled;
			this.write();
		}
		
		public boolean showCrosshairFarther()
		{
			return this.showCrosshairFarther;
		}
		
		public void setShowCrosshairFarther(boolean enabled)
		{
			this.showCrosshairFarther = enabled;
			this.write();
		}
		
		public boolean keepCameraOutOfHead()
		{
			return this.keepCameraOutOfHead;
		}
		
		public void setKeepCameraOutOfHead(boolean enabled)
		{
			this.keepCameraOutOfHead = enabled;
			this.write();
		}
		
		public boolean showAttackIndicator()
		{
			return this.attackIndicator;
		}
		
		public void setShowAttackIndicator(boolean enabled)
		{
			this.attackIndicator = enabled;
			this.write();
		}
		
		public boolean ignoreBlocksWithoutCollision()
		{
			return this.ignoreBlocksWithoutCollision;
		}
		
		public void setIgnoreBlocksWithoutCollision(boolean enabled)
		{
			this.ignoreBlocksWithoutCollision = enabled;
			this.write();
		}
		
		public boolean replaceDefaultPerspective()
		{
			return this.replaceDefaultPerspective;
		}
		
		public void setReplaceDefaultPerspective(boolean enabled)
		{
			this.replaceDefaultPerspective = enabled;
			this.write();
		}
		
		public String getDefaultPerspective()
		{
			return this.defaultPerspective;
		}
		
		public int getShoulderSurfing3ppId()
		{
			if(this.replaceDefaultPerspective())
			{
				return 1;
			}
			
			return 3;
		}
		
		public void adjustCameraLeft()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw() < this.getRotationMax())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw() + 0.5F);
			}
		}
		
		public void adjustCameraRight()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw() > this.getRotationMin())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw() - 0.5F);
			}
		}
		
		public void adjustCameraIn()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod() < this.getZoomMax())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod() + 0.01F);
			}
		}
		
		public void adjustCameraOut()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod() > this.getZoomMin())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod() - 0.01F);
			}
		}
		
		public void swapShoulder()
		{
			this.setShoulderRotationYaw(-this.getShoulderRotationYaw());
		}
	}
	
	protected static <T> void set(ForgeConfigSpec.ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			Config.CONFIG_DATA.set(configValue.getPath(), value);
		}
	}
	
	public static void configLoad(ModConfig.Loading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT))
		{
			Config.MOD_CONFIG = event.getConfig();
			Config.CONFIG_DATA = (CommentedFileConfig) Config.MOD_CONFIG.getConfigData();
			Config.CLIENT.read();
		}
	}
	
	public static void configReload(ModConfig.ConfigReloading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT) && Config.CONFIG_DATA != null)
		{
			Config.CONFIG_DATA.load();
			Config.CLIENT.read();
			
			if(Minecraft.getInstance().gameSettings.thirdPersonView == 3 && Config.CLIENT.replaceDefaultPerspective())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = 1;
			}
			else if(Minecraft.getInstance().gameSettings.thirdPersonView == 1 && !Config.CLIENT.replaceDefaultPerspective())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = 3;
			}
		}
	}
}
