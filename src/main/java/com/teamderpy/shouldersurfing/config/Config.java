package com.teamderpy.shouldersurfing.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Registry;
import org.apache.commons.lang3.tuple.Pair;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config
{
	public static ForgeConfigSpec CLIENT_SPEC;
	public static ClientConfig CLIENT;
	
	public static ForgeConfigSpec setup() {
		Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
		return CLIENT_SPEC;
	}
	
	public static class ClientConfig
	{
		private final DoubleValue offsetX;
		private final DoubleValue offsetY;
		private final DoubleValue offsetZ;
		
		private final DoubleValue minOffsetX;
		private final DoubleValue minOffsetY;
		private final DoubleValue minOffsetZ;
		
		private final DoubleValue maxOffsetX;
		private final DoubleValue maxOffsetY;
		private final DoubleValue maxOffsetZ;
		
		private final BooleanValue unlimitedOffsetX;
		private final BooleanValue unlimitedOffsetY;
		private final BooleanValue unlimitedOffsetZ;
		
		private final BooleanValue keepCameraOutOfHead;
		private final BooleanValue replaceDefaultPerspective;
		private final BooleanValue rememberLastPerspective;
		private final BooleanValue limitPlayerReach;
		private final DoubleValue cameraStepSize;
		private final ConfigValue<Perspective> defaultPerspective;
		
		private final ConfigValue<CrosshairType> crosshairType;
		private final DoubleValue customRaytraceDistance;
		private final BooleanValue useCustomRaytraceDistance;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairItems;
		private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("perspective");
			builder.push("offset");
			
			this.offsetX = builder
					.comment("Third person camera x-offset")
					.translation("x-offset")
					.defineInRange("offset_x", -0.75D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.offsetY = builder
					.comment("Third person camera y-offset")
					.translation("y-offset")
					.defineInRange("offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.offsetZ = builder
					.comment("Third person camera z-offset")
					.translation("z-offset")
					.defineInRange("offset_z", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.push("min");
			
			this.minOffsetX = builder
					.comment("If x-offset is limited this is the minimum amount")
					.translation("Minimum x-offset")
					.defineInRange("min_offset_x", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetY = builder
					.comment("If y-offset is limited this is the minimum amount")
					.translation("Minimum y-offset")
					.defineInRange("min_offset_y", -1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetZ = builder
					.comment("If z-offset is limited this is the minimum amount")
					.translation("Minimum z-offset")
					.defineInRange("min_offset_z", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("max");
			
			this.maxOffsetX = builder
					.comment("If x-offset is limited this is the maximum amount")
					.translation("Maximum x-offset")
					.defineInRange("max_offset_x", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetY = builder
					.comment("If y-offset is limited this is the maximum amount")
					.translation("Maximum y-offset")
					.defineInRange("max_offset_y", 1.5D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetZ = builder
					.comment("If z-offset is limited this is the maximum amount")
					.translation("Maximum z-offset")
					.defineInRange("max_offset_z", 5.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("limits");
			
			this.unlimitedOffsetX = builder
					.comment("Whether or not x-offset adjustment has limits")
					.translation("Unlimited x-offset")
					.define("unlimited_offset_x", false);
			
			this.unlimitedOffsetY = builder
					.comment("Whether or not y-offset adjustment has limits")
					.translation("Unlimited y-offset")
					.define("unlimited_offset_y", false);
			
			this.unlimitedOffsetZ = builder
					.comment("Whether or not z-offset adjustment has limits")
					.translation("Unlimited z-Offset")
					.define("unlimited_offset_z", false);
			
			builder.pop();
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
			
			this.limitPlayerReach = builder
					.comment("Whether or not to limit the player reach depending on the crosshair location (perspective offset)")
					.translation("Limit player reach")
					.define("limit_player_reach", true);
			
			this.cameraStepSize = builder
					.comment("Size of the camera adjustment per step")
					.translation("Camera step size")
					.defineInRange("camera_step_size", 0.025D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("crosshair");
			
			this.crosshairType = builder
					.comment("Crosshair type to use for shoulder surfing")
					.translation("Crosshair type")
					.defineEnum("crosshair_type", CrosshairType.ADAPTIVE, CrosshairType.values());
			
			this.customRaytraceDistance = builder
					.comment("The raytrace distance used for the dynamic crosshair")
					.translation("Custom Raytrace Distance")
					.defineInRange("custom_raytrace_distance", 400, 0, Double.MAX_VALUE);
			
			this.useCustomRaytraceDistance = builder
					.comment("Whether or not to use the custom raytrace distance used for the dynamic crosshair")
					.translation("Use Custom Raytrace Distance")
					.define("use_custom_raytrace_distance", true);
			
			this.adaptiveCrosshairItems = builder
					.comment("Additional items that trigger the dynamic crosshair when in apative mode")
					.translation("Adaptive Crosshair Items")
					.defineList("adaptive_crosshair_items", () ->
					{
						List<String> items = new ArrayList<String>();
						items.add(Registry.ITEM.getKey(Items.SNOWBALL).toString());
						items.add(Registry.ITEM.getKey(Items.EGG).toString());
						items.add(Registry.ITEM.getKey(Items.EXPERIENCE_BOTTLE).toString());
						items.add(Registry.ITEM.getKey(Items.ENDER_PEARL).toString());
						items.add(Registry.ITEM.getKey(Items.SPLASH_POTION).toString());
						items.add(Registry.ITEM.getKey(Items.FISHING_ROD).toString());
						items.add(Registry.ITEM.getKey(Items.LINGERING_POTION).toString());
						return items;
					}, item -> item != null && ResourceLocation.isValidResourceLocation(item.toString()));
			
			builder.push("visibility");
			
			for(Perspective entry : Perspective.values())
			{
				ConfigValue<CrosshairVisibility> crosshairVisibility = builder
						.comment("Crosshair visibility for " + entry.toString().toLowerCase())
						.translation(entry.toString() + " Crosshair Visibility")
						.defineEnum(entry.toString().toLowerCase(), entry.getDefaultCrosshairVisibility(), CrosshairVisibility.values());
				this.crosshairVisibility.put(entry, crosshairVisibility);
			}
			
			builder.pop();
			builder.pop();
		}
		
		public double getOffsetX()
		{
			return this.offsetX.get();
		}
		
		public void setOffsetX(double offsetX)
		{
			Config.set(this.offsetX, offsetX);
		}
		
		public double getOffsetY()
		{
			return this.offsetY.get();
		}
		
		public void setOffsetY(double offsetY)
		{
			Config.set(this.offsetY, offsetY);
		}
		
		public double getOffsetZ()
		{
			return this.offsetZ.get();
		}
		
		public void setOffsetZ(double offsetZ)
		{
			Config.set(this.offsetZ, offsetZ);
		}
		
		public double getMinOffsetX()
		{
			return this.minOffsetX.get();
		}
		
		public void setMinOffsetX(double minOffsetX)
		{
			Config.set(this.minOffsetX, minOffsetX);
		}
		
		public double getMinOffsetY()
		{
			return this.minOffsetY.get();
		}
		
		public void setMinOffsetY(double minOffsetY)
		{
			Config.set(this.minOffsetY, minOffsetY);
		}
		
		public double getMinOffsetZ()
		{
			return this.minOffsetZ.get();
		}
		
		public void setMinOffsetZ(double minOffsetZ)
		{
			Config.set(this.minOffsetZ, minOffsetZ);
		}
		
		public double getMaxOffsetX()
		{
			return this.maxOffsetX.get();
		}
		
		public void setMaxOffsetX(double maxOffsetX)
		{
			Config.set(this.maxOffsetX, maxOffsetX);
		}
		
		public double getMaxOffsetY()
		{
			return this.maxOffsetY.get();
		}
		
		public void setMaxOffsetY(double maxOffsetY)
		{
			Config.set(this.maxOffsetY, maxOffsetY);
		}
		
		public double getMaxOffsetZ()
		{
			return this.maxOffsetZ.get();
		}
		
		public void setMaxOffsetZ(double maxOffsetZ)
		{
			Config.set(this.maxOffsetZ, maxOffsetZ);
		}
		
		public boolean isUnlimitedOffsetX()
		{
			return this.unlimitedOffsetX.get();
		}
		
		public void setUnlimitedOffsetX(boolean unlimitedOffsetX)
		{
			Config.set(this.unlimitedOffsetX, unlimitedOffsetX);
		}
		
		public boolean isUnlimitedOffsetY()
		{
			return this.unlimitedOffsetY.get();
		}
		
		public void setUnlimitedOffsetY(boolean unlimitedOffsetY)
		{
			Config.set(this.unlimitedOffsetY, unlimitedOffsetY);
		}
		
		public boolean isUnlimitedOffsetZ()
		{
			return this.unlimitedOffsetZ.get();
		}
		
		public void setUnlimitedOffsetZ(boolean unlimitedOffsetZ)
		{
			Config.set(this.unlimitedOffsetZ, unlimitedOffsetZ);
		}
		
		public CrosshairVisibility getCrosshairVisibility(Perspective perspective)
		{
			return this.crosshairVisibility.get(perspective).get();
		}
		
		public void setCrosshairVisibility(Perspective perspective, CrosshairVisibility visibility)
		{
			Config.set(this.crosshairVisibility.get(perspective), visibility);
		}
		
		public boolean useCustomRaytraceDistance()
		{
			return this.useCustomRaytraceDistance.get();
		}
		
		public void setUseCustomRaytraceDistance(boolean useCustomRaytraceDistance)
		{
			Config.set(this.useCustomRaytraceDistance, useCustomRaytraceDistance);
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
		
		public double getCameraStepSize()
		{
			return this.cameraStepSize.get();
		}
		
		public void setCameraStepSize(double cameraStepSize)
		{
			Config.set(this.cameraStepSize, cameraStepSize);
		}
		
		public double getCustomRaytraceDistance()
		{
			return this.customRaytraceDistance.get();
		}
		
		public void setCustomRaytraceDistance(double raytraceDistance)
		{
			Config.set(this.customRaytraceDistance, raytraceDistance);
		}
		
		public boolean limitPlayerReach()
		{
			return this.limitPlayerReach.get();
		}
		
		public void setLimitPlayerReach(boolean limitPlayerReach)
		{
			Config.set(this.limitPlayerReach, limitPlayerReach);
		}
		
		public List<? extends String> getAdaptiveCrosshairItems()
		{
			return this.adaptiveCrosshairItems.get();
		}
		
		public void adjustCameraLeft()
		{
			this.setOffsetX(this.addStep(this.getOffsetX(), this.getMaxOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraRight()
		{
			this.setOffsetX(this.subStep(this.getOffsetX(), this.getMinOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraUp()
		{
			this.setOffsetY(this.addStep(this.getOffsetY(), this.getMaxOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraDown()
		{
			this.setOffsetY(this.subStep(this.getOffsetY(), this.getMinOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraIn()
		{
			this.setOffsetZ(this.subStep(this.getOffsetZ(), this.getMinOffsetZ(), this.isUnlimitedOffsetZ()));
		}
		
		public void adjustCameraOut()
		{
			this.setOffsetZ(this.addStep(this.getOffsetZ(), this.getMaxOffsetZ(), this.isUnlimitedOffsetZ()));
		}
		
		private double addStep(double value, double max, boolean unlimited)
		{
			double next = value + this.getCameraStepSize();
			
			if(unlimited)
			{
				return next;
			}
			
			return Math.min(next, max);
		}
		
		private double subStep(double value, double min, boolean unlimited)
		{
			double next = value - this.getCameraStepSize();
			
			if(unlimited)
			{
				return next;
			}
			
			return Math.max(next, min);
		}
		
		public void swapShoulder()
		{
			this.setOffsetX(-this.getOffsetX());
		}
	}
	
	protected static <T> void set(ForgeConfigSpec.ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			configValue.set(value);
		}
	}
}
