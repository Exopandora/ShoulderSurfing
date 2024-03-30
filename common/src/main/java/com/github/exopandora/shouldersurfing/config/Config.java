package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Config
{
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	static
	{
		Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
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
		
		private final DoubleValue passengerOffsetXMultiplier;
		private final DoubleValue passengerOffsetYMultiplier;
		private final DoubleValue passengerOffsetZMultiplier;
		
		private final DoubleValue sprintOffsetXMultiplier;
		private final DoubleValue sprintOffsetYMultiplier;
		private final DoubleValue sprintOffsetZMultiplier;
		
		private final DoubleValue keepCameraOutOfHeadMultiplier;
		private final BooleanValue replaceDefaultPerspective;
		private final BooleanValue skipThirdPersonFront;
		private final BooleanValue rememberLastPerspective;
		private final BooleanValue limitPlayerReach;
		private final DoubleValue cameraStepSize;
		private final ConfigValue<Perspective> defaultPerspective;
		private final BooleanValue centerCameraWhenClimbing;
		private final DoubleValue cameraTransitionSpeedMultiplier;
		private final DoubleValue centerCameraWhenLookingDownAngle;
		private final DoubleValue hidePlayerWhenLookingUpAngle;
		private final BooleanValue dynamicallyAdjustOffsets;
		private final BooleanValue playerTransparency;
		private final BooleanValue isCameraDecoupled;
		
		private final ConfigValue<CrosshairType> crosshairType;
		private final DoubleValue customRaytraceDistance;
		private final BooleanValue useCustomRaytraceDistance;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItems;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItems;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemProperties;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemProperties;
		private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("perspective");
			builder.push("offset");
			
			this.offsetX = builder
				.comment("Third person camera x-offset.")
				.translation("x-offset")
				.defineInRange("offset_x", -0.75D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.offsetY = builder
				.comment("Third person camera y-offset.")
				.translation("y-offset")
				.defineInRange("offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.offsetZ = builder
				.comment("Third person camera z-offset.")
				.translation("z-offset")
				.defineInRange("offset_z", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.push("min");
			
			this.minOffsetX = builder
				.comment("If x-offset is limited this is the minimum amount.")
				.translation("Minimum x-offset")
				.defineInRange("min_offset_x", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetY = builder
				.comment("If y-offset is limited this is the minimum amount.")
				.translation("Minimum y-offset")
				.defineInRange("min_offset_y", -1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetZ = builder
				.comment("If z-offset is limited this is the minimum amount.")
				.translation("Minimum z-offset")
				.defineInRange("min_offset_z", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("max");
			
			this.maxOffsetX = builder
				.comment("If x-offset is limited this is the maximum amount.")
				.translation("Maximum x-offset")
				.defineInRange("max_offset_x", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetY = builder
				.comment("If y-offset is limited this is the maximum amount.")
				.translation("Maximum y-offset")
				.defineInRange("max_offset_y", 1.5D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetZ = builder
				.comment("If z-offset is limited this is the maximum amount.")
				.translation("Maximum z-offset")
				.defineInRange("max_offset_z", 5.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("limits");
			
			this.unlimitedOffsetX = builder
				.comment("Whether or not x-offset adjustment has limits.")
				.translation("Unlimited x-offset")
				.define("unlimited_offset_x", false);
			
			this.unlimitedOffsetY = builder
				.comment("Whether or not y-offset adjustment has limits.")
				.translation("Unlimited y-offset")
				.define("unlimited_offset_y", false);
			
			this.unlimitedOffsetZ = builder
				.comment("Whether or not z-offset adjustment has limits.")
				.translation("Unlimited z-offset")
				.define("unlimited_offset_z", false);
			
			builder.pop();
			builder.push("multiplier");
			builder.push("passenger");
			
			this.passengerOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is a passenger.")
				.translation("Passenger x-offset multiplier")
				.defineInRange("multiplier_offset_x", 1.0D, 0, Double.MAX_VALUE);
			
			this.passengerOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is a passenger.")
				.translation("Passenger y-offset multiplier")
				.defineInRange("multiplier_offset_y", 1.0D, 0, Double.MAX_VALUE);
			
			this.passengerOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is a passenger.")
				.translation("Passenger z-offset multiplier")
				.defineInRange("multiplier_offset_z", 1.0D, 0, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("sprint");
			
			this.sprintOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is sprinting.")
				.translation("Sprint x-offset multiplier")
				.defineInRange("multiplier_offset_x", 1.0D, 0, Double.MAX_VALUE);
			
			this.sprintOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is sprinting.")
				.translation("Sprint y-offset multiplier")
				.defineInRange("multiplier_offset_y", 1.0D, 0, Double.MAX_VALUE);
			
			this.sprintOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is sprinting.")
				.translation("Sprint z-offset multiplier")
				.defineInRange("multiplier_offset_z", 1.0D, 0, Double.MAX_VALUE);
			
			builder.pop();
			builder.pop();
			builder.pop();
			
			this.keepCameraOutOfHeadMultiplier = builder
				.comment("The distance multiplier on whether or not to hide the player model if the camera gets too close to it. Set to 0 to disable.")
				.translation("Keep camera out of head distance multiplier")
				.defineInRange("keep_camera_out_of_head_distance_multiplier", 0.75D, 0D, Double.MAX_VALUE);
			
			this.defaultPerspective = builder
				.comment("The default perspective when you load the game.")
				.translation("Default perspective")
				.defineEnum("default_perspective", Perspective.SHOULDER_SURFING, Perspective.values());
			
			this.rememberLastPerspective = builder
				.comment("Whether or not to remember the last perspective used.")
				.translation("Remember last perspective")
				.define("remember_last_perspective", true);
			
			this.replaceDefaultPerspective = builder
				.comment("Whether or not to replace the default third person perspective.")
				.translation("Replace default perspective")
				.define("replace_default_perspective", false);
			
			this.skipThirdPersonFront = builder
				.comment("Whether or not to skip the third person front perspective.")
				.translation("Skip third person front perspective")
				.define("skip_third_person_front_perspective", false);
			
			this.limitPlayerReach = builder
				.comment("Whether or not to limit the player reach depending on the crosshair location (perspective offset).")
				.translation("Limit player reach")
				.define("limit_player_reach", true);
			
			this.cameraStepSize = builder
				.comment("Size of the camera adjustment per step.")
				.translation("Camera step size")
				.defineInRange("camera_step_size", 0.025D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.centerCameraWhenClimbing = builder
				.comment("Whether or not to temporarily center the camera when climbing.")
				.translation("Center camera when climbing")
				.define("center_camera_when_climbing", true);
			
			this.cameraTransitionSpeedMultiplier = builder
				.comment("The speed multiplier at which the camera transitions between positions.")
				.translation("Camera transition speed multiplier")
				.defineInRange("camera_transition_speed_multiplier", 0.25D, 0.05D, 1.0D);
			
			this.centerCameraWhenLookingDownAngle = builder
				.comment("The angle at which the camera will be centered when looking down. Set to 0 to disable.")
				.translation("Center camera when looking down angle")
				.defineInRange("center_camera_when_looking_down_angle", 1D, 0D, 90D);
			
			this.hidePlayerWhenLookingUpAngle = builder
				.comment("The angle at which the player will no longer be rendered when looking up. Set to 0 to disable.")
				.translation("Center camera when looking down angle")
				.defineInRange("hide_player_when_looking_up_angle", 15D, 0D, 90D);
			
			this.dynamicallyAdjustOffsets = builder
				.comment("Whether or not to dynamically adjust camera offsets depending on space constraints.")
				.translation("Dynamically adjust offsets")
				.define("dynamically_adjust_offsets", true);
			
			this.playerTransparency = builder
				.comment("Whether or not to adjust the player model transparency when view is obstructed.")
				.translation("Adjust player transparency")
				.define("adjust_player_transparency", true);
			
			this.isCameraDecoupled = builder
				.comment("Whether or not to decouple the camera rotation from the player rotation.")
				.translation("Decoupled camera")
				.define("decoupled_camera", true);
			
			builder.pop();
			builder.push("crosshair");
			
			this.crosshairType = builder
				.comment("Crosshair type to use for shoulder surfing.")
				.translation("Crosshair type")
				.defineEnum("crosshair_type", CrosshairType.STATIC, CrosshairType.values());
			
			this.customRaytraceDistance = builder
				.comment("The raytrace distance used for the dynamic crosshair.")
				.translation("Custom raytrace distance")
				.defineInRange("custom_raytrace_distance", 400, 0, Double.MAX_VALUE);
			
			this.useCustomRaytraceDistance = builder
				.comment("Whether or not to use the custom raytrace distance used for the dynamic crosshair.")
				.translation("Use custom raytrace distance")
				.define("use_custom_raytrace_distance", true);
			
			this.adaptiveCrosshairHoldItems = builder
				.comment("Items that when held, trigger the dynamic crosshair in adaptive mode. This config option supports regex expressions. Example: 'minecraft:.*stone' matches 'minecraft:stone' and 'minecraft:cobblestone'.")
				.translation("Adaptive crosshair items (hold)")
				.defineList("adaptive_crosshair_hold_items", () ->
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
				}, Objects::nonNull);
			
			this.adaptiveCrosshairUseItems = builder
				.comment("Items that when used, trigger the dynamic crosshair in adaptive mode. This config option supports regex expressions. Example: 'minecraft:.*stone' matches 'minecraft:stone' and 'minecraft:cobblestone'.")
				.translation("Adaptive crosshair items (use)")
				.defineList("adaptive_crosshair_use_items", ArrayList::new, Objects::nonNull);
			
			this.adaptiveCrosshairHoldItemProperties = builder
				.comment("Item properties of an item, that when held, trigger the dynamic crosshair in adaptive mode.")
				.translation("Adaptive crosshair item properties (hold)")
				.defineList("adaptive_crosshair_hold_item_properties", () ->
				{
					List<String> items = new ArrayList<String>();
					items.add(new ResourceLocation("charged").toString());
					return items;
				}, item -> item != null && ResourceLocation.isValidResourceLocation(item.toString()));
			
			this.adaptiveCrosshairUseItemProperties = builder
				.comment("Item properties of an item, that when used, trigger the dynamic crosshair in adaptive mode.")
				.translation("Adaptive crosshair item properties (use)")
				.defineList("adaptive_crosshair_use_item_properties", () ->
				{
					List<String> items = new ArrayList<String>();
					items.add(new ResourceLocation("pull").toString());
					items.add(new ResourceLocation("throwing").toString());
					return items;
				}, item -> item != null && ResourceLocation.isValidResourceLocation(item.toString()));
			
			builder.push("visibility");
			
			for(Perspective entry : Perspective.values())
			{
				ConfigValue<CrosshairVisibility> crosshairVisibility = builder
					.comment("Crosshair visibility for " + entry.toString().toLowerCase() + ".")
					.translation(entry + " crosshair visibility")
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
		
		public double getPassengerOffsetXMultiplier()
		{
			return this.passengerOffsetXMultiplier.get();
		}
		
		public void setPassengerOffsetXMultiplier(double passengerOffsetXMultiplier)
		{
			Config.set(this.passengerOffsetXMultiplier, passengerOffsetXMultiplier);
		}
		
		public double getPassengerOffsetYMultiplier()
		{
			return this.passengerOffsetYMultiplier.get();
		}
		
		public void setPassengerOffsetYMultiplier(double passengerOffsetYMultiplier)
		{
			Config.set(this.passengerOffsetYMultiplier, passengerOffsetYMultiplier);
		}
		
		public double getPassengerOffsetZMultiplier()
		{
			return this.passengerOffsetZMultiplier.get();
		}
		
		public void setPassengerOffsetZMultiplier(double passengerOffsetZMultiplier)
		{
			Config.set(this.passengerOffsetZMultiplier, passengerOffsetZMultiplier);
		}
		
		public double getSprintOffsetXMultiplier()
		{
			return this.sprintOffsetXMultiplier.get();
		}
		
		public void setSprintOffsetXMultiplier(double sprintOffsetXMultiplier)
		{
			Config.set(this.sprintOffsetXMultiplier, sprintOffsetXMultiplier);
		}
		
		public double getSprintOffsetYMultiplier()
		{
			return this.sprintOffsetYMultiplier.get();
		}
		
		public void setSprintOffsetYMultiplier(double sprintOffsetYMultiplier)
		{
			Config.set(this.sprintOffsetYMultiplier, sprintOffsetYMultiplier);
		}
		
		public double getSprintOffsetZMultiplier()
		{
			return this.sprintOffsetZMultiplier.get();
		}
		
		public void setSprintOffsetZMultiplier(double sprintOffsetZMultiplier)
		{
			Config.set(this.sprintOffsetZMultiplier, sprintOffsetZMultiplier);
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
		
		public double keepCameraOutOfHeadMultiplier()
		{
			return this.keepCameraOutOfHeadMultiplier.get();
		}
		
		public void setKeepCameraOutOfHeadMultiplier(double multiplier)
		{
			Config.set(this.keepCameraOutOfHeadMultiplier, multiplier);
		}
		
		public boolean replaceDefaultPerspective()
		{
			return this.replaceDefaultPerspective.get();
		}
		
		public void setReplaceDefaultPerspective(boolean enabled)
		{
			Config.set(this.replaceDefaultPerspective, enabled);
		}
		
		public boolean skipThirdPersonFront()
		{
			return this.skipThirdPersonFront.get();
		}
		
		public void setSkipThirdPersonFront(boolean skipThirdPersonFront)
		{
			Config.set(this.skipThirdPersonFront, skipThirdPersonFront);
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
		
		public boolean doCenterCameraWhenClimbing()
		{
			return this.centerCameraWhenClimbing.get();
		}
		
		public void setCenterCameraWhenClimbing(boolean enabled)
		{
			Config.set(this.centerCameraWhenClimbing, enabled);
		}
		
		public double getCameraTransitionSpeedMultiplier()
		{
			return this.cameraTransitionSpeedMultiplier.get();
		}
		
		public void setCameraInterpolationSpeedMultiplier(double cameraTransitionSpeedMultiplier)
		{
			Config.set(this.cameraTransitionSpeedMultiplier, cameraTransitionSpeedMultiplier);
		}
		
		public double getCenterCameraWhenLookingDownAngle()
		{
			return this.centerCameraWhenLookingDownAngle.get();
		}
		
		public void setCenterCameraWhenLookingDown(double centerCameraWhenLookingDownAngle)
		{
			Config.set(this.centerCameraWhenLookingDownAngle, centerCameraWhenLookingDownAngle);
		}
		
		public double getHidePlayerWhenLookingUpAngle()
		{
			return this.hidePlayerWhenLookingUpAngle.get();
		}
		
		public void setHidePlayerWhenLookingUpAngle(double hidePlayerWhenLookingUpAngle)
		{
			Config.set(this.hidePlayerWhenLookingUpAngle, hidePlayerWhenLookingUpAngle);
		}
		
		public boolean doDynamicallyAdjustOffsets()
		{
			return this.dynamicallyAdjustOffsets.get();
		}
		
		public void setDynamicallyAdjustOffsets(boolean dynamicallyAdjustOffsets)
		{
			Config.set(this.dynamicallyAdjustOffsets, dynamicallyAdjustOffsets);
		}
		
		public boolean isPlayerTransparencyEnabled()
		{
			return this.playerTransparency.get();
		}
		
		public void setPlayerTransparencyEnabled(boolean enabled)
		{
			Config.set(this.playerTransparency, enabled);
		}
		
		public boolean isCameraDecoupled()
		{
			return this.isCameraDecoupled.get();
		}
		
		public void setIsCameraDecoupled(boolean enabled)
		{
			Config.set(this.isCameraDecoupled, enabled);
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
		
		public List<? extends String> getAdaptiveCrosshairHoldItems()
		{
			return this.adaptiveCrosshairHoldItems.get();
		}
		
		public List<? extends String> getAdaptiveCrosshairUseItems()
		{
			return this.adaptiveCrosshairUseItems.get();
		}
		
		public List<? extends String> getAdaptiveCrosshairHoldItemProperties()
		{
			return this.adaptiveCrosshairHoldItemProperties.get();
		}
		
		public List<? extends String> getAdaptiveCrosshairUseItemProperties()
		{
			return this.adaptiveCrosshairUseItemProperties.get();
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
	
	public static void onConfigReload(ModConfig config)
	{
		if(ShoulderInstance.getInstance() != null && Type.CLIENT.equals(config.getType()) && Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(Perspective.current());
		}
		
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null && !Config.CLIENT.isCameraDecoupled())
		{
			ShoulderRenderer.getInstance().resetState(cameraEntity);
		}
	}
}
