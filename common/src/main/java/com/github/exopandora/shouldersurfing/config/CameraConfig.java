package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.ICameraConfig;
import com.github.exopandora.shouldersurfing.api.model.CameraDistanceAttributeMode;
import com.github.exopandora.shouldersurfing.api.model.ViewBobbingMode;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import java.util.ArrayList;
import java.util.List;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class CameraConfig implements ICameraConfig {
	private final DoubleValue offsetX;
	private final DoubleValue offsetY;
	private final DoubleValue offsetZ;
	
	private final ConfigValue<List<? extends String>> offsetXPresets;
	private final ConfigValue<List<? extends String>> offsetYPresets;
	private final ConfigValue<List<? extends String>> offsetZPresets;
	
	private final DoubleValue minOffsetX;
	private final DoubleValue minOffsetY;
	private final DoubleValue minOffsetZ;
	
	private final DoubleValue maxOffsetX;
	private final DoubleValue maxOffsetY;
	private final DoubleValue maxOffsetZ;
	
	private final BooleanValue isOffsetXUnlimited;
	private final BooleanValue isOffsetYUnlimited;
	private final BooleanValue isOffsetZUnlimited;
	
	private final DoubleValue passengerOffsetXMultiplier;
	private final DoubleValue passengerOffsetYMultiplier;
	private final DoubleValue passengerOffsetZMultiplier;
	
	private final DoubleValue sprintOffsetXMultiplier;
	private final DoubleValue sprintOffsetYMultiplier;
	private final DoubleValue sprintOffsetZMultiplier;
	
	private final DoubleValue aimingOffsetXMultiplier;
	private final DoubleValue aimingOffsetYMultiplier;
	private final DoubleValue aimingOffsetZMultiplier;
	
	private final DoubleValue fallFlyingOffsetXMultiplier;
	private final DoubleValue fallFlyingOffsetYMultiplier;
	private final DoubleValue fallFlyingOffsetZMultiplier;
	
	private final DoubleValue climbingOffsetXMultiplier;
	private final DoubleValue climbingOffsetYMultiplier;
	private final DoubleValue climbingOffsetZMultiplier;
	
	private final DoubleValue passengerOffsetXModifier;
	private final DoubleValue passengerOffsetYModifier;
	private final DoubleValue passengerOffsetZModifier;
	
	private final DoubleValue sprintOffsetXModifier;
	private final DoubleValue sprintOffsetYModifier;
	private final DoubleValue sprintOffsetZModifier;
	
	private final DoubleValue aimingOffsetXModifier;
	private final DoubleValue aimingOffsetYModifier;
	private final DoubleValue aimingOffsetZModifier;
	
	private final DoubleValue fallFlyingOffsetXModifier;
	private final DoubleValue fallFlyingOffsetYModifier;
	private final DoubleValue fallFlyingOffsetZModifier;
	
	private final DoubleValue climbingOffsetXModifier;
	private final DoubleValue climbingOffsetYModifier;
	private final DoubleValue climbingOffsetZModifier;
	
	private final ConfigValue<CameraDistanceAttributeMode> cameraDistanceAttributeMode;
	
	private final DoubleValue keepCameraOutOfHeadMultiplier;
	private final DoubleValue offsetStepSize;
	private final DoubleValue cameraTransitionSpeedMultiplier;
	private final DoubleValue centerCameraWhenLookingDownAngle;
	private final BooleanValue isOffsetDynamic;
	private final BooleanValue isCameraDecoupled;
	private final BooleanValue orientCameraOnTeleport;
	private final BooleanValue isFovOverrideEnabled;
	private final DoubleValue fovOverride;
	private final ConfigValue<ViewBobbingMode> viewBobbingMode;
	private final BooleanValue followPlayerRotations;
	private final IntValue followPlayerRotationsDelay;
	
	private final DoubleValue cameraDragXMultiplier;
	private final DoubleValue cameraDragYMultiplier;
	private final DoubleValue cameraDragZMultiplier;
	
	private final DoubleValue cameraSwayXMaxAngle;
	private final DoubleValue cameraSwayZMaxAngle;
	private final DoubleValue cameraSwayXMaxVelocity;
	private final DoubleValue cameraSwayZMaxVelocity;
	
	protected CameraConfig(ModConfigSpec.Builder builder) {
		builder.push("camera");
		builder.push("offset");
		
		this.offsetX = builder
			.comment("Third person camera x-offset.")
			.translation(MOD_ID + ".configuration.camera.offset.offset_x")
			.defineInRange("offset_x", -0.75D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.offsetY = builder
			.comment("Third person camera y-offset.")
			.translation(MOD_ID + ".configuration.camera.offset.offset_y")
			.defineInRange("offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.offsetZ = builder
			.comment("Third person camera z-offset.")
			.translation(MOD_ID + ".configuration.camera.offset.offset_z")
			.defineInRange("offset_z", 4.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.push("presets");
		
		this.offsetXPresets = builder
			.comment("A list of x-offset presets that can be toggled via the 'Toggle X-Offset Presets' keybind. WARNING: Duplicate entries can result in undefined behavior!")
			.translation(MOD_ID + ".configuration.camera.offset.presets.offset_x_presets")
			.defineList("presets_offset_x", ArrayList::new, String::new, Config.ClientConfig::isValidDouble);
		
		this.offsetYPresets = builder
			.comment("A list of y-offset presets that can be toggled via the 'Toggle Y-Offset Presets' keybind. WARNING: Duplicate entries can result in undefined behavior!")
			.translation(MOD_ID + ".configuration.camera.offset.presets.offset_y_presets")
			.defineList("presets_offset_y", ArrayList::new, String::new, Config.ClientConfig::isValidDouble);
		
		this.offsetZPresets = builder
			.comment("A list of z-offset presets that can be toggled via the 'Toggle Z-Offset Presets' keybind. WARNING: Duplicate entries can result in undefined behavior!")
			.translation(MOD_ID + ".configuration.camera.offset.presets.offset_z_presets")
			.defineList("presets_offset_z", ArrayList::new, String::new, Config.ClientConfig::isValidDouble);
		
		builder.pop();
		builder.push("min");
		
		this.minOffsetX = builder
			.comment("When x-offset is limited this is the minimum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.min.min_offset_x")
			.defineInRange("min_offset_x", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.minOffsetY = builder
			.comment("When y-offset is limited this is the minimum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.min.min_offset_y")
			.defineInRange("min_offset_y", -1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.minOffsetZ = builder
			.comment("When z-offset is limited this is the minimum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.min.min_offset_z")
			.defineInRange("min_offset_z", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("max");
		
		this.maxOffsetX = builder
			.comment("When x-offset is limited this is the maximum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.max.max_offset_x")
			.defineInRange("max_offset_x", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.maxOffsetY = builder
			.comment("When y-offset is limited this is the maximum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.max.max_offset_y")
			.defineInRange("max_offset_y", 1.5D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.maxOffsetZ = builder
			.comment("When z-offset is limited this is the maximum amount.")
			.translation(MOD_ID + ".configuration.camera.offset.max.max_offset_z")
			.defineInRange("max_offset_z", 5.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("limits");
		
		this.isOffsetXUnlimited = builder
			.comment("Whether x-offset adjustment has limits.")
			.translation(MOD_ID + ".configuration.camera.offset.limits.unlimited_offset_x")
			.define("unlimited_offset_x", false);
		
		this.isOffsetYUnlimited = builder
			.comment("Whether y-offset adjustment has limits.")
			.translation(MOD_ID + ".configuration.camera.offset.limits.unlimited_offset_y")
			.define("unlimited_offset_y", false);
		
		this.isOffsetZUnlimited = builder
			.comment("Whether z-offset adjustment has limits.")
			.translation(MOD_ID + ".configuration.camera.offset.limits.unlimited_offset_z")
			.define("unlimited_offset_z", false);
		
		builder.pop();
		builder.push("multiplier");
		builder.push("passenger");
		
		this.passengerOffsetXMultiplier = builder
			.comment("x-offset multiplier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.passenger.multiplier_offset_x")
			.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.passengerOffsetYMultiplier = builder
			.comment("y-offset multiplier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.passenger.multiplier_offset_y")
			.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.passengerOffsetZMultiplier = builder
			.comment("z-offset multiplier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.passenger.multiplier_offset_z")
			.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("sprint");
		
		this.sprintOffsetXMultiplier = builder
			.comment("x-offset multiplier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.sprint.multiplier_offset_x")
			.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.sprintOffsetYMultiplier = builder
			.comment("y-offset multiplier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.sprint.multiplier_offset_y")
			.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.sprintOffsetZMultiplier = builder
			.comment("z-offset multiplier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.sprint.multiplier_offset_z")
			.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("aiming");
		
		this.aimingOffsetXMultiplier = builder
			.comment("x-offset multiplier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.aiming.multiplier_offset_x")
			.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.aimingOffsetYMultiplier = builder
			.comment("y-offset multiplier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.aiming.multiplier_offset_y")
			.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.aimingOffsetZMultiplier = builder
			.comment("z-offset multiplier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.aiming.multiplier_offset_z")
			.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("fall_flying");
		
		this.fallFlyingOffsetXMultiplier = builder
			.comment("x-offset multiplier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.fall_flying.multiplier_offset_x")
			.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.fallFlyingOffsetYMultiplier = builder
			.comment("y-offset multiplier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.fall_flying.multiplier_offset_y")
			.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.fallFlyingOffsetZMultiplier = builder
			.comment("z-offset multiplier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.fall_flying.multiplier_offset_z")
			.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("climbing");
		
		this.climbingOffsetXMultiplier = builder
			.comment("x-offset multiplier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.climbing.multiplier_offset_x")
			.defineInRange("multiplier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.climbingOffsetYMultiplier = builder
			.comment("y-offset multiplier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.climbing.multiplier_offset_y")
			.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.climbingOffsetZMultiplier = builder
			.comment("z-offset multiplier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.multiplier.climbing.multiplier_offset_z")
			.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.pop();
		builder.push("modifiers");
		builder.push("passenger");
		
		this.passengerOffsetXModifier = builder
			.comment("x-offset modifier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.passenger.modifier_offset_x")
			.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.passengerOffsetYModifier = builder
			.comment("y-offset modifier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.passenger.modifier_offset_y")
			.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.passengerOffsetZModifier = builder
			.comment("z-offset modifier for when the player is a passenger.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.passenger.modifier_offset_z")
			.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("sprint");
		
		this.sprintOffsetXModifier = builder
			.comment("x-offset modifier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.sprint.modifier_offset_x")
			.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.sprintOffsetYModifier = builder
			.comment("y-offset modifier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.sprint.modifier_offset_y")
			.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.sprintOffsetZModifier = builder
			.comment("z-offset modifier for when the player is sprinting.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.sprint.modifier_offset_z")
			.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("aiming");
		
		this.aimingOffsetXModifier = builder
			.comment("x-offset modifier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.aiming.modifier_offset_x")
			.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.aimingOffsetYModifier = builder
			.comment("y-offset modifier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.aiming.modifier_offset_y")
			.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.aimingOffsetZModifier = builder
			.comment("z-offset modifier for when the player is aiming.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.aiming.modifier_offset_z")
			.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("fall_flying");
		
		this.fallFlyingOffsetXModifier = builder
			.comment("x-offset modifier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.fall_flying.modifier_offset_x")
			.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.fallFlyingOffsetYModifier = builder
			.comment("y-offset modifier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.fall_flying.modifier_offset_y")
			.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.fallFlyingOffsetZModifier = builder
			.comment("z-offset modifier for when using Elytra.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.fall_flying.modifier_offset_z")
			.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.push("climbing");
		
		this.climbingOffsetXModifier = builder
			.comment("x-offset modifier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.climbing.modifier_offset_x")
			.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.climbingOffsetYModifier = builder
			.comment("y-offset modifier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.climbing.modifier_offset_y")
			.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.climbingOffsetZModifier = builder
			.comment("z-offset modifier for when the player is climbing.")
			.translation(MOD_ID + ".configuration.camera.offset.modifier.climbing.modifier_offset_z")
			.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		builder.pop();
		
		this.isOffsetDynamic = builder
			.comment("Whether to dynamically adjust camera offsets depending on space constraints.")
			.translation(MOD_ID + ".configuration.camera.offset.dynamic_offsets")
			.define("dynamic_offsets", true);
		
		this.offsetStepSize = builder
			.comment("Size of the offset adjustment per step.")
			.translation(MOD_ID + ".configuration.camera.offset.step_size")
			.defineInRange("step_size", 0.025D, -Double.MAX_VALUE, Double.MAX_VALUE);
		
		builder.pop();
		
		this.keepCameraOutOfHeadMultiplier = builder
			.comment("The distance multiplier on whether to hide the player model if the camera gets too close to it. Set to 0 to disable.")
			.translation(MOD_ID + ".configuration.camera.keep_camera_out_of_head_distance_multiplier")
			.defineInRange("keep_camera_out_of_head_distance_multiplier", 0.75D, 0D, Double.MAX_VALUE);
		
		this.cameraTransitionSpeedMultiplier = builder
			.comment("The speed multiplier at which the camera transitions between positions.")
			.translation(MOD_ID + ".configuration.camera.camera_transition_speed_multiplier")
			.defineInRange("camera_transition_speed_multiplier", 0.25D, 0.05D, 1.0D);
		
		this.centerCameraWhenLookingDownAngle = builder
			.comment("The angle at which the camera will be centered when looking down. Set to 0 to disable.")
			.translation(MOD_ID + ".configuration.camera.center_camera_when_looking_down_angle")
			.defineInRange("center_camera_when_looking_down_angle", 1D, 0D, 90D);
		
		this.cameraDistanceAttributeMode = builder
			.comment("Defines how to handle the camera distance attribute.")
			.translation(MOD_ID + ".configuration.camera.camera_distance_attribute_mode")
			.defineEnum("camera_distance_attribute_mode", CameraDistanceAttributeMode.RELATIVE, CameraDistanceAttributeMode.values());
		
		this.isCameraDecoupled = builder
			.comment("Whether to decouple the camera rotation from the player rotation.")
			.translation(MOD_ID + ".configuration.camera.decoupled_camera")
			.define("decoupled_camera", true);
		
		this.orientCameraOnTeleport = builder
			.comment("Whether to orient the camera rotation when the player is teleported. This includes passenger (dis-)mounting and traveling through portals.")
			.translation(MOD_ID + ".configuration.camera.orient_camera_on_teleport")
			.define("orient_camera_on_teleport", true);
		
		builder.push("fov");
		
		this.isFovOverrideEnabled = builder
			.comment("Whether to apply the FOV override when in shoulder surfing perspective.")
			.translation(MOD_ID + ".configuration.camera.fov.fov_override_enabled")
			.define("fov_override_enabled", false);
		
		this.fovOverride = builder
			.comment("The camera FOV when in shoulder surfing perspective. Depends on config option 'fov_override_enabled'.")
			.translation(MOD_ID + ".configuration.camera.fov.fov_override")
			.defineInRange("fov_override", 70.0D, 30.0D, 110.0D);
		
		builder.pop();
		
		this.viewBobbingMode = builder
			.comment("Whether to apply view bobbing in shoulder surfing perspective. Set to INHERIT to inherit vanilla setting.")
			.translation(MOD_ID + ".configuration.camera.view_bobbing_mode")
			.defineEnum("view_bobbing_mode", ViewBobbingMode.INHERIT, ViewBobbingMode.values());
		
		this.followPlayerRotations = builder
			.comment("Whether to follow the rotations of the player when camera is decoupled.")
			.translation(MOD_ID + ".configuration.camera.follow_player_rotations")
			.define("follow_player_rotations", true);
		
		this.followPlayerRotationsDelay = builder
			.comment("The delay in ticks after which the camera rotations will follow rotations of the player.")
			.translation(MOD_ID + ".configuration.camera.follow_player_rotations_delay")
			.defineInRange("follow_player_rotations_delay", 40, 1, Integer.MAX_VALUE);
		
		builder.push("camera_drag");
		
		this.cameraDragXMultiplier = builder
			.comment("x-axis multiplier for camera drag.")
			.translation(MOD_ID + ".configuration.camera.camera_drag.multiplier_axis_x")
			.defineInRange("multiplier_axis_x", 0.0D, 0, 5);
		
		this.cameraDragYMultiplier = builder
			.comment("y-axis multiplier for camera drag.")
			.translation(MOD_ID + ".configuration.camera.camera_drag.multiplier_axis_y")
			.defineInRange("multiplier_axis_y", 0.0D, 0, 5);
		
		this.cameraDragZMultiplier = builder
			.comment("z-axis multiplier for camera drag.")
			.translation(MOD_ID + ".configuration.camera.camera_drag.multiplier_axis_z")
			.defineInRange("multiplier_axis_z", 0.0D, 0, 5);
		
		builder.pop();
		builder.push("camera_sway");
		
		this.cameraSwayXMaxAngle = builder
			.comment("The maximum x-axis angle in degrees. Set to 0 to disable.")
			.translation(MOD_ID + ".configuration.camera.camera_sway.max_angle_axis_x")
			.defineInRange("max_angle_axis_x", 0.0D, -30, 30);
		
		this.cameraSwayZMaxAngle = builder
			.comment("The maximum x-axis angle in degrees. Set to 0 to disable.")
			.translation(MOD_ID + ".configuration.camera.camera_sway.max_angle_axis_z")
			.defineInRange("max_angle_axis_z", 0.0D, -30, 30);
		
		this.cameraSwayXMaxVelocity = builder
			.comment("The velocity of the player in blocks per second, where the maximum camera x-axis sway is applied.")
			.translation(MOD_ID + ".configuration.camera.camera_sway.max_velocity_axis_x")
			.defineInRange("max_velocity_axis_x", 5, 0.05, 1000);
		
		this.cameraSwayZMaxVelocity = builder
			.comment("The velocity of the player in blocks per second, where the maximum camera z-axis sway is applied.")
			.translation(MOD_ID + ".configuration.camera.camera_sway.max_velocity_axis_z")
			.defineInRange("max_velocity_axis_z", 5, 0.05, 1000);
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public double getOffsetX() {
		return this.offsetX.get();
	}
	
	@Override
	public double getOffsetY() {
		return this.offsetY.get();
	}
	
	@Override
	public double getOffsetZ() {
		return this.offsetZ.get();
	}
	
	@Override
	public List<Double> getOffsetXPresets() {
		return this.offsetXPresets.get().stream().map(Double::parseDouble).toList();
	}
	
	@Override
	public List<Double> getOffsetYPresets() {
		return this.offsetYPresets.get().stream().map(Double::parseDouble).toList();
	}
	
	@Override
	public List<Double> getOffsetZPresets() {
		return this.offsetZPresets.get().stream().map(Double::parseDouble).toList();
	}
	
	@Override
	public double getMinOffsetX() {
		return this.minOffsetX.get();
	}
	
	@Override
	public double getMinOffsetY() {
		return this.minOffsetY.get();
	}
	
	@Override
	public double getMinOffsetZ() {
		return this.minOffsetZ.get();
	}
	
	@Override
	public double getMaxOffsetX() {
		return this.maxOffsetX.get();
	}
	
	@Override
	public double getMaxOffsetY() {
		return this.maxOffsetY.get();
	}
	
	@Override
	public double getMaxOffsetZ() {
		return this.maxOffsetZ.get();
	}
	
	@Override
	public boolean isOffsetXUnlimited() {
		return this.isOffsetXUnlimited.get();
	}
	
	@Override
	public boolean isOffsetYUnlimited() {
		return this.isOffsetYUnlimited.get();
	}
	
	@Override
	public boolean isOffsetZUnlimited() {
		return this.isOffsetZUnlimited.get();
	}
	
	@Override
	public double getPassengerOffsetXMultiplier() {
		return this.passengerOffsetXMultiplier.get();
	}
	
	@Override
	public double getPassengerOffsetYMultiplier() {
		return this.passengerOffsetYMultiplier.get();
	}
	
	@Override
	public double getPassengerOffsetZMultiplier() {
		return this.passengerOffsetZMultiplier.get();
	}
	
	@Override
	public double getSprintOffsetXMultiplier() {
		return this.sprintOffsetXMultiplier.get();
	}
	
	@Override
	public double getSprintOffsetYMultiplier() {
		return this.sprintOffsetYMultiplier.get();
	}
	
	@Override
	public double getSprintOffsetZMultiplier() {
		return this.sprintOffsetZMultiplier.get();
	}
	
	@Override
	public double getAimingOffsetXMultiplier() {
		return this.aimingOffsetXMultiplier.get();
	}
	
	@Override
	public double getAimingOffsetYMultiplier() {
		return this.aimingOffsetYMultiplier.get();
	}
	
	@Override
	public double getAimingOffsetZMultiplier() {
		return this.aimingOffsetZMultiplier.get();
	}
	
	@Override
	public double getFallFlyingOffsetXMultiplier() {
		return this.fallFlyingOffsetXMultiplier.get();
	}
	
	@Override
	public double getFallFlyingOffsetYMultiplier() {
		return this.fallFlyingOffsetYMultiplier.get();
	}
	
	@Override
	public double getFallFlyingOffsetZMultiplier() {
		return this.fallFlyingOffsetZMultiplier.get();
	}
	
	@Override
	public double getClimbingOffsetXMultiplier() {
		return this.climbingOffsetXMultiplier.get();
	}
	
	@Override
	public double getClimbingOffsetYMultiplier() {
		return this.climbingOffsetYMultiplier.get();
	}
	
	@Override
	public double getClimbingOffsetZMultiplier() {
		return this.climbingOffsetZMultiplier.get();
	}
	
	@Override
	public double getPassengerOffsetXModifier() {
		return this.passengerOffsetXModifier.get();
	}
	
	@Override
	public double getPassengerOffsetYModifier() {
		return this.passengerOffsetYModifier.get();
	}
	
	@Override
	public double getPassengerOffsetZModifier() {
		return this.passengerOffsetZModifier.get();
	}
	
	@Override
	public double getSprintOffsetXModifier() {
		return this.sprintOffsetXModifier.get();
	}
	
	@Override
	public double getSprintOffsetYModifier() {
		return this.sprintOffsetYModifier.get();
	}
	
	@Override
	public double getSprintOffsetZModifier() {
		return this.sprintOffsetZModifier.get();
	}
	
	@Override
	public double getAimingOffsetXModifier() {
		return this.aimingOffsetXModifier.get();
	}
	
	@Override
	public double getAimingOffsetYModifier() {
		return this.aimingOffsetYModifier.get();
	}
	
	@Override
	public double getAimingOffsetZModifier() {
		return this.aimingOffsetZModifier.get();
	}
	
	@Override
	public double getFallFlyingOffsetXModifier() {
		return this.fallFlyingOffsetXModifier.get();
	}
	
	@Override
	public double getFallFlyingOffsetYModifier() {
		return this.fallFlyingOffsetYModifier.get();
	}
	
	@Override
	public double getFallFlyingOffsetZModifier() {
		return this.fallFlyingOffsetZModifier.get();
	}
	
	@Override
	public double getClimbingOffsetXModifier() {
		return this.climbingOffsetXModifier.get();
	}
	
	@Override
	public double getClimbingOffsetYModifier() {
		return this.climbingOffsetYModifier.get();
	}
	
	@Override
	public double getClimbingOffsetZModifier() {
		return this.climbingOffsetZModifier.get();
	}
	
	@Override
	public CameraDistanceAttributeMode getCameraDistanceAttributeMode() {
		return this.cameraDistanceAttributeMode.get();
	}
	
	@Override
	public double keepCameraOutOfHeadMultiplier() {
		return this.keepCameraOutOfHeadMultiplier.get();
	}
	
	@Override
	public double getCameraStepSize() {
		return this.offsetStepSize.get();
	}
	
	@Override
	public double getCameraTransitionSpeedMultiplier() {
		return this.cameraTransitionSpeedMultiplier.get();
	}
	
	@Override
	public double getCenterCameraWhenLookingDownAngle() {
		return this.centerCameraWhenLookingDownAngle.get();
	}
	
	@Override
	public boolean isOffsetDynamic() {
		return this.isOffsetDynamic.get();
	}
	
	@Override
	public boolean isCameraDecoupled() {
		return this.isCameraDecoupled.get();
	}
	
	@Override
	public boolean doOrientCameraOnTeleport() {
		return this.orientCameraOnTeleport.get();
	}
	
	@Override
	public boolean isFovOverrideEnabled() {
		return this.isFovOverrideEnabled.get();
	}
	
	@Override
	public float getFovOverride() {
		return this.fovOverride.get().floatValue();
	}
	
	@Override
	public ViewBobbingMode getViewBobbingMode() {
		return this.viewBobbingMode.get();
	}
	
	@Override
	public boolean getFollowPlayerRotations() {
		return this.followPlayerRotations.get();
	}
	
	@Override
	public int getFollowPlayerRotationsDelay() {
		return this.followPlayerRotationsDelay.get();
	}
	
	@Override
	public double getCameraDragXMultiplier() {
		return this.cameraDragXMultiplier.get();
	}
	
	@Override
	public double getCameraDragYMultiplier() {
		return this.cameraDragYMultiplier.get();
	}
	
	@Override
	public double getCameraDragZMultiplier() {
		return this.cameraDragZMultiplier.get();
	}
	
	@Override
	public double getCameraSwayXMaxAngle() {
		return this.cameraSwayXMaxAngle.get();
	}
	
	@Override
	public double getCameraSwayZMaxAngle() {
		return this.cameraSwayZMaxAngle.get();
	}
	
	@Override
	public double getCameraSwayXMaxVelocity() {
		return this.cameraSwayXMaxVelocity.get();
	}
	
	@Override
	public double getCameraSwayZMaxVelocity() {
		return this.cameraSwayZMaxVelocity.get();
	}
	
	public void adjustCameraLeft() {
		Config.CLIENT.set(this.offsetX, this.addStep(this.getOffsetX(), this.getMaxOffsetX(), this.isOffsetXUnlimited()));
	}
	
	public void adjustCameraRight() {
		Config.CLIENT.set(this.offsetX, this.subStep(this.getOffsetX(), this.getMinOffsetX(), this.isOffsetXUnlimited()));
	}
	
	public void adjustCameraUp() {
		Config.CLIENT.set(this.offsetY, this.addStep(this.getOffsetY(), this.getMaxOffsetY(), this.isOffsetYUnlimited()));
	}
	
	public void adjustCameraDown() {
		Config.CLIENT.set(this.offsetY, this.subStep(this.getOffsetY(), this.getMinOffsetY(), this.isOffsetYUnlimited()));
	}
	
	public void adjustCameraIn() {
		Config.CLIENT.set(this.offsetZ, this.subStep(this.getOffsetZ(), this.getMinOffsetZ(), this.isOffsetZUnlimited()));
	}
	
	public void adjustCameraOut() {
		Config.CLIENT.set(this.offsetZ, this.addStep(this.getOffsetZ(), this.getMaxOffsetZ(), this.isOffsetZUnlimited()));
	}
	
	public void toggleOffsetXPreset() {
		this.toggleOffsetPreset(this.offsetX, this.getOffsetXPresets());
	}
	
	public void toggleOffsetYPreset() {
		this.toggleOffsetPreset(this.offsetY, this.getOffsetYPresets());
	}
	
	public void toggleOffsetZPreset() {
		this.toggleOffsetPreset(this.offsetZ, this.getOffsetZPresets());
	}
	
	private void toggleOffsetPreset(DoubleValue offset, List<Double> presets) {
		if (presets.isEmpty()) {
			return;
		}
		int closestIndex = 0;
		double currentOffset = offset.get();
		double distance = Math.abs(currentOffset - presets.getFirst());
		for (int x = 1; x < presets.size(); x++) {
			double preset = presets.get(x);
			double newDistance = Math.abs(currentOffset - preset);
			
			if (newDistance <= distance) {
				closestIndex = x;
				distance = newDistance;
			}
		}
		double newOffset;
		if (closestIndex == presets.size() - 1) {
			newOffset = presets.getFirst();
		} else {
			newOffset = presets.get(closestIndex + 1);
		}
		Config.CLIENT.set(offset, newOffset);
	}
	
	private double addStep(double value, double max, boolean unlimited) {
		double next = value + this.getCameraStepSize();
		if (unlimited) {
			return next;
		}
		return Math.min(next, max);
	}
	
	private double subStep(double value, double min, boolean unlimited) {
		double next = value - this.getCameraStepSize();
		if (unlimited) {
			return next;
		}
		return Math.max(next, min);
	}
	
	public void swapShoulder() {
		Config.CLIENT.set(this.offsetX, -this.getOffsetX());
	}
	
	public void toggleCameraCoupling() {
		Config.CLIENT.set(this.isCameraDecoupled, !this.isCameraDecoupled());
	}
}
