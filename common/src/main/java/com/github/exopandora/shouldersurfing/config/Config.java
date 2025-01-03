package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickOrigin;
import com.github.exopandora.shouldersurfing.api.model.PickVector;
import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

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
	
	public static class ClientConfig implements IClientConfig
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
		
		private final DoubleValue keepCameraOutOfHeadMultiplier;
		private final DoubleValue cameraStepSize;
		private final DoubleValue cameraTransitionSpeedMultiplier;
		private final DoubleValue centerCameraWhenLookingDownAngle;
		private final BooleanValue dynamicallyAdjustOffsets;
		private final BooleanValue isCameraDecoupled;
		
		private final BooleanValue replaceDefaultPerspective;
		private final BooleanValue isFirstPersonEnabled;
		private final BooleanValue isThirdPersonFrontEnabled;
		private final BooleanValue isThirdPersonBackEnabled;
		private final ConfigValue<Perspective> defaultPerspective;
		private final BooleanValue rememberLastPerspective;
		
		private final BooleanValue playerTransparency;
		private final DoubleValue hidePlayerWhenLookingUpAngle;
		private final ConfigValue<TurningMode> turningModeWhenUsingItem;
		private final ConfigValue<TurningMode> turningModeWhenAttacking;
		private final ConfigValue<TurningMode> turningModeWhenInteraction;
		private final ConfigValue<TurningMode> turningModeWhenPicking;
		private final IntValue turningLockTime;
		private final BooleanValue playerXRotFollowsCamera;
		private final BooleanValue playerYRotFollowsCamera;
		private final DoubleValue playerYRotFollowAngleLimit;
		private final ConfigValue<PickOrigin> entityPickOrigin;
		private final ConfigValue<PickOrigin> blockPickOrigin;
		private final ConfigValue<PickVector> pickVector;
		
		private final ConfigValue<CrosshairType> crosshairType;
		private final DoubleValue customRaytraceDistance;
		private final BooleanValue useCustomRaytraceDistance;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItems;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItems;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemProperties;
		private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemProperties;
		private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();

		private final BooleanValue showObstructionIndicator;
		private final BooleanValue showObstructionIndicatorWhenAiming;
		private final IntValue obstructionIndicatorMinDistanceToCrosshair;
		private final DoubleValue obstructionIndicatorMaxDistanceToObstruction;
		
		private final BooleanValue centerPlayerSounds;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
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
			
			builder.push("min");
			
			this.minOffsetX = builder
				.comment("When x-offset is limited this is the minimum amount.")
				.translation(MOD_ID + ".configuration.offset.min.min_offset_x")
				.defineInRange("min_offset_x", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetY = builder
				.comment("When y-offset is limited this is the minimum amount.")
				.translation(MOD_ID + ".configuration.offset.min.min_offset_y")
				.defineInRange("min_offset_y", -1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.minOffsetZ = builder
				.comment("When z-offset is limited this is the minimum amount.")
				.translation(MOD_ID + ".configuration.offset.min.min_offset_z")
				.defineInRange("min_offset_z", -3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("max");
			
			this.maxOffsetX = builder
				.comment("When x-offset is limited this is the maximum amount.")
				.translation(MOD_ID + ".configuration.offset.max.max_offset_x")
				.defineInRange("max_offset_x", 3.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetY = builder
				.comment("When y-offset is limited this is the maximum amount.")
				.translation(MOD_ID + ".configuration.offset.max.max_offset_y")
				.defineInRange("max_offset_y", 1.5D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.maxOffsetZ = builder
				.comment("When z-offset is limited this is the maximum amount.")
				.translation(MOD_ID + ".configuration.offset.max.max_offset_z")
				.defineInRange("max_offset_z", 5.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("limits");
			
			this.unlimitedOffsetX = builder
				.comment("Whether or not x-offset adjustment has limits.")
				.translation(MOD_ID + ".configuration.offset.limits.unlimited_offset_x")
				.define("unlimited_offset_x", false);
			
			this.unlimitedOffsetY = builder
				.comment("Whether or not y-offset adjustment has limits.")
				.translation(MOD_ID + ".configuration.offset.limits.unlimited_offset_y")
				.define("unlimited_offset_y", false);
			
			this.unlimitedOffsetZ = builder
				.comment("Whether or not z-offset adjustment has limits.")
				.translation(MOD_ID + ".configuration.offset.limits.unlimited_offset_z")
				.define("unlimited_offset_z", false);
			
			builder.pop();
			builder.push("multiplier");
			builder.push("passenger");
			
			this.passengerOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.multiplier.passenger.multiplier_offset_x")
				.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.passengerOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.multiplier.passenger.multiplier_offset_y")
				.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.passengerOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.multiplier.passenger.multiplier_offset_z")
				.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("sprint");
			
			this.sprintOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.multiplier.sprint.multiplier_offset_x")
				.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.sprintOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.multiplier.sprint.multiplier_offset_y")
				.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.sprintOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.multiplier.sprint.multiplier_offset_z")
				.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("aiming");
			
			this.aimingOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.multiplier.aiming.multiplier_offset_x")
				.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.aimingOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.multiplier.aiming.multiplier_offset_y")
				.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.aimingOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.multiplier.aiming.multiplier_offset_z")
				.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("fall_flying");
			
			this.fallFlyingOffsetXMultiplier = builder
				.comment("x-offset multiplier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.multiplier.fall_flying.multiplier_offset_x")
				.defineInRange("multiplier_offset_x", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.fallFlyingOffsetYMultiplier = builder
				.comment("y-offset multiplier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.multiplier.fall_flying.multiplier_offset_y")
				.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.fallFlyingOffsetZMultiplier = builder
				.comment("z-offset multiplier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.multiplier.fall_flying.multiplier_offset_z")
				.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("climbing");
			
			this.climbingOffsetXMultiplier = builder
				.comment("x-offset multiplier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.multiplier.climbing.multiplier_offset_x")
				.defineInRange("multiplier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.climbingOffsetYMultiplier = builder
				.comment("y-offset multiplier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.multiplier.climbing.multiplier_offset_y")
				.defineInRange("multiplier_offset_y", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.climbingOffsetZMultiplier = builder
				.comment("z-offset multiplier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.multiplier.climbing.multiplier_offset_z")
				.defineInRange("multiplier_offset_z", 1.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.pop();
			builder.push("modifiers");
			builder.push("passenger");
			
			this.passengerOffsetXModifier = builder
				.comment("x-offset modifier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.modifier.passenger.modifier_offset_x")
				.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.passengerOffsetYModifier = builder
				.comment("y-offset modifier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.modifier.passenger.modifier_offset_y")
				.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.passengerOffsetZModifier = builder
				.comment("z-offset modifier for when the player is a passenger.")
				.translation(MOD_ID + ".configuration.offset.modifier.passenger.modifier_offset_z")
				.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("sprint");
			
			this.sprintOffsetXModifier = builder
				.comment("x-offset modifier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.modifier.sprint.modifier_offset_x")
				.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.sprintOffsetYModifier = builder
				.comment("y-offset modifier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.modifier.sprint.modifier_offset_y")
				.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.sprintOffsetZModifier = builder
				.comment("z-offset modifier for when the player is sprinting.")
				.translation(MOD_ID + ".configuration.offset.modifier.sprint.modifier_offset_z")
				.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("aiming");
			
			this.aimingOffsetXModifier = builder
				.comment("x-offset modifier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.modifier.aiming.modifier_offset_x")
				.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.aimingOffsetYModifier = builder
				.comment("y-offset modifier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.modifier.aiming.modifier_offset_y")
				.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.aimingOffsetZModifier = builder
				.comment("z-offset modifier for when the player is aiming.")
				.translation(MOD_ID + ".configuration.offset.modifier.aiming.modifier_offset_z")
				.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("fall_flying");
			
			this.fallFlyingOffsetXModifier = builder
				.comment("x-offset modifier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.modifier.fall_flying.modifier_offset_x")
				.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.fallFlyingOffsetYModifier = builder
				.comment("y-offset modifier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.modifier.fall_flying.modifier_offset_y")
				.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.fallFlyingOffsetZModifier = builder
				.comment("z-offset modifier for when using Elytra.")
				.translation(MOD_ID + ".configuration.offset.modifier.fall_flying.modifier_offset_z")
				.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("climbing");
			
			this.climbingOffsetXModifier = builder
				.comment("x-offset modifier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.modifier.climbing.modifier_offset_x")
				.defineInRange("modifier_offset_x", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.climbingOffsetYModifier = builder
				.comment("y-offset modifier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.modifier.climbing.modifier_offset_y")
				.defineInRange("modifier_offset_y", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.climbingOffsetZModifier = builder
				.comment("z-offset modifier for when the player is climbing.")
				.translation(MOD_ID + ".configuration.offset.modifier.climbing.modifier_offset_z")
				.defineInRange("modifier_offset_z", 0.0D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			builder.pop();
			builder.pop();
			builder.pop();
			
			this.keepCameraOutOfHeadMultiplier = builder
				.comment("The distance multiplier on whether or not to hide the player model if the camera gets too close to it. Set to 0 to disable.")
				.translation(MOD_ID + ".configuration.camera.keep_camera_out_of_head_distance_multiplier")
				.defineInRange("keep_camera_out_of_head_distance_multiplier", 0.75D, 0D, Double.MAX_VALUE);
			
			this.cameraStepSize = builder
				.comment("Size of the camera adjustment per step.")
				.translation(MOD_ID + ".configuration.camera.camera_step_size")
				.defineInRange("camera_step_size", 0.025D, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.cameraTransitionSpeedMultiplier = builder
				.comment("The speed multiplier at which the camera transitions between positions.")
				.translation(MOD_ID + ".configuration.camera.camera_transition_speed_multiplier")
				.defineInRange("camera_transition_speed_multiplier", 0.25D, 0.05D, 1.0D);
			
			this.centerCameraWhenLookingDownAngle = builder
				.comment("The angle at which the camera will be centered when looking down. Set to 0 to disable.")
				.translation(MOD_ID + ".configuration.camera.center_camera_when_looking_down_angle")
				.defineInRange("center_camera_when_looking_down_angle", 1D, 0D, 90D);
			
			this.dynamicallyAdjustOffsets = builder
				.comment("Whether or not to dynamically adjust camera offsets depending on space constraints.")
				.translation(MOD_ID + ".configuration.camera.dynamically_adjust_offsets")
				.define("dynamically_adjust_offsets", true);
			
			this.isCameraDecoupled = builder
				.comment("Whether or not to decouple the camera rotation from the player rotation.")
				.translation(MOD_ID + ".configuration.camera.decoupled_camera")
				.define("decoupled_camera", true);
			
			builder.pop();
			builder.push("perspective");
			
			this.defaultPerspective = builder
				.comment("The default perspective when you load the game.")
				.translation(MOD_ID + ".configuration.perspective.default_perspective")
				.defineEnum("default_perspective", Perspective.SHOULDER_SURFING, Perspective.values());
			
			this.rememberLastPerspective = builder
				.comment("Whether or not to remember the last perspective used.")
				.translation(MOD_ID + ".configuration.perspective.remember_last_perspective")
				.define("remember_last_perspective", true);
			
			this.replaceDefaultPerspective = builder
				.comment("Whether or not to replace the default third person perspective.")
				.translation(MOD_ID + ".configuration.perspective.replace_default_perspective")
				.define("replace_default_perspective", false);
			
			this.isFirstPersonEnabled = builder
				.comment("Whether or not the first person perspective is enabled.")
				.translation(MOD_ID + ".configuration.perspective.first_person_enabled")
				.define("first_person_enabled", true);
			
			this.isThirdPersonFrontEnabled = builder
				.comment("Whether or not the third person front perspective is enabled.")
				.translation(MOD_ID + ".configuration.perspective.third_person_front_enabled")
				.define("third_person_front_enabled", true);
			
			this.isThirdPersonBackEnabled = builder
				.comment("Whether or not the third person back perspective is enabled.")
				.translation(MOD_ID + ".configuration.perspective.third_person_back_enabled")
				.define("third_person_back_enabled", true);
			
			builder.pop();
			builder.push("player");
			
			this.playerTransparency = builder
				.comment("Whether or not to adjust the player model transparency when view is obstructed. Changing this value may require a game restart to take full effect.")
				.translation(MOD_ID + ".configuration.player.adjust_player_transparency")
				.define("adjust_player_transparency", true);
			
			this.hidePlayerWhenLookingUpAngle = builder
				.comment("The angle at which the player will no longer be rendered when looking up. Set to 0 to disable.")
				.translation(MOD_ID + ".configuration.player.hide_player_when_looking_up_angle")
				.defineInRange("hide_player_when_looking_up_angle", 0D, 0D, 90D);
			
			this.playerXRotFollowsCamera = builder
				.comment("Whether the x-rot of the player should follow the camera x-rot. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.player_x_rot_follows_camera")
				.define("player_x_rot_follows_camera", false);
			
			this.playerYRotFollowsCamera = builder
				.comment("Whether the y-rot of the player should follow the camera y-rot. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.player_y_rot_follows_camera")
				.define("player_y_rot_follows_camera", false);
			
			this.playerYRotFollowAngleLimit = builder
				.comment("The maximum angle to which the player y-rot follows the camera y-rot. This config option only applies when player y-rot follows camera option is enabled.")
				.translation(MOD_ID + ".configuration.player.player_y_rot_follow_angle_limit")
				.defineInRange("player_y_rot_follow_angle_limit", 90D, 0D, 180D);
			
			builder.push("turning");
			
			this.turningModeWhenUsingItem = builder
				.comment("Whether to turn the player when using an item. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.turning.when_using_item")
				.defineEnum("when_using_item", TurningMode.ALWAYS, TurningMode.values());
			
			this.turningModeWhenAttacking = builder
				.comment("Whether to turn the player when attacking. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.turning.when_attacking")
				.defineEnum("when_attacking", TurningMode.REQUIRES_TARGET, TurningMode.values());
			
			this.turningModeWhenInteraction = builder
				.comment("Whether to turn the player when interacting with blocks. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.turning.when_interacting")
				.defineEnum("when_interacting", TurningMode.ALWAYS, TurningMode.values());
			
			this.turningModeWhenPicking = builder
				.comment("Whether to turn the player when picking blocks or entities. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.turning.when_picking")
				.defineEnum("when_picking", TurningMode.ALWAYS, TurningMode.values());
			
			this.turningLockTime = builder
				.comment("The time in ticks the player will remain turned after the interaction has ended. Set to 0 to disable. This config option only applies when camera is decoupled.")
				.translation(MOD_ID + ".configuration.player.turning.turning_lock_time")
				.defineInRange("turning_lock_time", 4, 0, Integer.MAX_VALUE);
			
			builder.pop();
			builder.pop();
			builder.push("object_picker");
			
			this.customRaytraceDistance = builder
				.comment("The raytrace distance used for the dynamic crosshair.")
				.translation(MOD_ID + ".configuration.object_picker.custom_raytrace_distance")
				.defineInRange("custom_raytrace_distance", 400, 0, Double.MAX_VALUE);
			
			this.useCustomRaytraceDistance = builder
				.comment("Whether or not to use the custom raytrace distance used for the dynamic crosshair.")
				.translation(MOD_ID + ".configuration.object_picker.use_custom_raytrace_distance")
				.define("use_custom_raytrace_distance", true);
			
			builder.push("pick_origin");
			
			this.entityPickOrigin = builder
				.comment("The origin where the entity pick starts when using the static crosshair.")
				.translation(MOD_ID + ".configuration.object_picker.pick_origin.entity_pick_origin")
				.defineEnum("entity_pick_origin", PickOrigin.PLAYER, PickOrigin.values());
			
			this.blockPickOrigin = builder
				.comment("The origin where the block pick starts when using the static crosshair.")
				.translation(MOD_ID + ".configuration.object_picker.pick_origin.block_pick_origin")
				.defineEnum("block_pick_origin", PickOrigin.PLAYER, PickOrigin.values());
			
			builder.pop();
			builder.push("pick_vector");
			
			this.pickVector = builder
				.comment("The vector direction of the raytrace when picking objects. This config option only applies when using the dynamic crosshair.")
				.translation(MOD_ID + ".configuration.object_picker.pick_vector.pick_vector")
				.defineEnum("pick_vector", PickVector.CAMERA, PickVector.values());
			
			builder.pop();
			builder.pop();
			builder.push("crosshair");
			
			this.crosshairType = builder
				.comment("Crosshair type to use for shoulder surfing.")
				.translation(MOD_ID + ".configuration.crosshair.crosshair_type")
				.defineEnum("crosshair_type", CrosshairType.STATIC, CrosshairType.values());
			
			this.adaptiveCrosshairHoldItems = builder
				.comment("Items that when held, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. Example: 'minecraft:.*sword' matches 'minecraft:wooden_sword' and 'minecraft:netherite_sword'.")
				.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_items")
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
				.comment("Items that when used, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. Example: 'minecraft:.*sword' matches 'minecraft:wooden_sword' and 'minecraft:netherite_sword'.")
				.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_items")
				.defineList("adaptive_crosshair_use_items", ArrayList::new, Objects::nonNull);
			
			this.adaptiveCrosshairHoldItemProperties = builder
				.comment("Item properties of an item, that when held, trigger the dynamic crosshair in adaptive mode.")
				.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_item_properties")
				.defineList("adaptive_crosshair_hold_item_properties", () ->
				{
					List<String> items = new ArrayList<String>();
					items.add(new ResourceLocation("charged").toString());
					return items;
				}, item -> item != null && ResourceLocation.isValidResourceLocation(item.toString()));
			
			this.adaptiveCrosshairUseItemProperties = builder
				.comment("Item properties of an item, that when used, trigger the dynamic crosshair in adaptive mode.")
				.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_item_properties")
				.defineList("adaptive_crosshair_use_item_properties", () ->
				{
					List<String> items = new ArrayList<String>();
					items.add(new ResourceLocation("pull").toString());
					items.add(new ResourceLocation("throwing").toString());
					return items;
				}, item -> item != null && ResourceLocation.isValidResourceLocation(item.toString()));
				
			builder.push("obstruction");
			
			this.showObstructionIndicator = builder
				.comment("When the crosshair type is static, shows an additional indicator on obstacles that stand between you and your target.")
				.translation(MOD_ID + ".configuration.obstruction.show_obstruction_indicator")
				.define("show_obstruction_indicator", true);
			
			this.showObstructionIndicatorWhenAiming = builder
				.comment("Only show the obstruction indicator when using items that would trigger the adaptive crosshair.")
				.translation(MOD_ID + ".configuration.obstruction.only_when_aiming")
				.define("only_when_aiming", true);
			
			this.obstructionIndicatorMinDistanceToCrosshair = builder
				.comment("Hide the obstruction indicator when it is too close to the main crosshair. Distance measured in scaled pixels.")
				.translation(MOD_ID + ".configuration.obstruction.min_distance_to_crosshair")
				.defineInRange("min_distance_to_crosshair", 8, 0, Integer.MAX_VALUE);
			
			this.obstructionIndicatorMaxDistanceToObstruction = builder
				.comment("Ignore obstructions that are too far away from the player. Distance measured in blocks. Set to 0 to disable.")
				.translation(MOD_ID + ".configuration.obstruction.max_distance_to_obstruction")
				.defineInRange("max_distance_to_obstruction", 20, 0, Double.MAX_VALUE);
			
			builder.pop();
			builder.push("visibility");
			
			for(Perspective entry : Perspective.values())
			{
				String key = entry.toString().toLowerCase();
				ConfigValue<CrosshairVisibility> crosshairVisibility = builder
					.comment("Crosshair visibility for " + key.replace('_', ' ') + ".")
					.translation(MOD_ID + ".configuration.crosshair.visibility." + key)
					.defineEnum(entry.toString().toLowerCase(), entry.getDefaultCrosshairVisibility(), CrosshairVisibility.values());
				this.crosshairVisibility.put(entry, crosshairVisibility);
			}
			
			builder.pop();
			builder.pop();
			builder.push("audio");
			
			this.centerPlayerSounds = builder
				.comment("Whether to center sounds made by the player.")
				.translation(MOD_ID + ".configuration.audio.center_player_sounds")
				.define("center_player_sounds", false);
			
			builder.pop();
		}
		
		@Override
		public double getOffsetX()
		{
			return this.offsetX.get();
		}
		
		@Override
		public double getOffsetY()
		{
			return this.offsetY.get();
		}
		
		@Override
		public double getOffsetZ()
		{
			return this.offsetZ.get();
		}
		
		@Override
		public double getMinOffsetX()
		{
			return this.minOffsetX.get();
		}
		
		@Override
		public double getMinOffsetY()
		{
			return this.minOffsetY.get();
		}
		
		@Override
		public double getMinOffsetZ()
		{
			return this.minOffsetZ.get();
		}
		
		@Override
		public double getMaxOffsetX()
		{
			return this.maxOffsetX.get();
		}
		
		@Override
		public double getMaxOffsetY()
		{
			return this.maxOffsetY.get();
		}
		
		@Override
		public double getMaxOffsetZ()
		{
			return this.maxOffsetZ.get();
		}
		
		@Override
		public boolean isUnlimitedOffsetX()
		{
			return this.unlimitedOffsetX.get();
		}
		
		@Override
		public boolean isUnlimitedOffsetY()
		{
			return this.unlimitedOffsetY.get();
		}
		
		@Override
		public boolean isUnlimitedOffsetZ()
		{
			return this.unlimitedOffsetZ.get();
		}
		
		@Override
		public double getPassengerOffsetXMultiplier()
		{
			return this.passengerOffsetXMultiplier.get();
		}
		
		@Override
		public double getPassengerOffsetYMultiplier()
		{
			return this.passengerOffsetYMultiplier.get();
		}
		
		@Override
		public double getPassengerOffsetZMultiplier()
		{
			return this.passengerOffsetZMultiplier.get();
		}
		
		@Override
		public double getSprintOffsetXMultiplier()
		{
			return this.sprintOffsetXMultiplier.get();
		}
		
		@Override
		public double getSprintOffsetYMultiplier()
		{
			return this.sprintOffsetYMultiplier.get();
		}
		
		@Override
		public double getSprintOffsetZMultiplier()
		{
			return this.sprintOffsetZMultiplier.get();
		}
		
		@Override
		public double getAimingOffsetXMultiplier()
		{
			return this.aimingOffsetXMultiplier.get();
		}
		
		@Override
		public double getAimingOffsetYMultiplier()
		{
			return this.aimingOffsetYMultiplier.get();
		}
		
		@Override
		public double getAimingOffsetZMultiplier()
		{
			return this.aimingOffsetZMultiplier.get();
		}
		
		@Override
		public double getFallFlyingOffsetXMultiplier()
		{
			return this.fallFlyingOffsetXMultiplier.get();
		}
		
		@Override
		public double getFallFlyingOffsetYMultiplier()
		{
			return this.fallFlyingOffsetYMultiplier.get();
		}
		
		@Override
		public double getFallFlyingOffsetZMultiplier()
		{
			return this.fallFlyingOffsetZMultiplier.get();
		}
		
		@Override
		public double getClimbingOffsetXMultiplier()
		{
			return this.climbingOffsetXMultiplier.get();
		}
		
		@Override
		public double getClimbingOffsetYMultiplier()
		{
			return this.climbingOffsetYMultiplier.get();
		}
		
		@Override
		public double getClimbingOffsetZMultiplier()
		{
			return this.climbingOffsetZMultiplier.get();
		}
		
		@Override
		public double getPassengerOffsetXModifier()
		{
			return this.passengerOffsetXModifier.get();
		}
		
		@Override
		public double getPassengerOffsetYModifier()
		{
			return this.passengerOffsetYModifier.get();
		}
		
		@Override
		public double getPassengerOffsetZModifier()
		{
			return this.passengerOffsetZModifier.get();
		}
		
		@Override
		public double getSprintOffsetXModifier()
		{
			return this.sprintOffsetXModifier.get();
		}
		
		@Override
		public double getSprintOffsetYModifier()
		{
			return this.sprintOffsetYModifier.get();
		}
		
		@Override
		public double getSprintOffsetZModifier()
		{
			return this.sprintOffsetZModifier.get();
		}
		
		@Override
		public double getAimingOffsetXModifier()
		{
			return this.aimingOffsetXModifier.get();
		}
		
		@Override
		public double getAimingOffsetYModifier()
		{
			return this.aimingOffsetYModifier.get();
		}
		
		@Override
		public double getAimingOffsetZModifier()
		{
			return this.aimingOffsetZModifier.get();
		}
		
		@Override
		public double getFallFlyingOffsetXModifier()
		{
			return this.fallFlyingOffsetXModifier.get();
		}
		
		@Override
		public double getFallFlyingOffsetYModifier()
		{
			return this.fallFlyingOffsetYModifier.get();
		}
		
		@Override
		public double getFallFlyingOffsetZModifier()
		{
			return this.fallFlyingOffsetZModifier.get();
		}
		
		@Override
		public double getClimbingOffsetXModifier()
		{
			return this.climbingOffsetXModifier.get();
		}
		
		@Override
		public double getClimbingOffsetYModifier()
		{
			return this.climbingOffsetYModifier.get();
		}
		
		@Override
		public double getClimbingOffsetZModifier()
		{
			return this.climbingOffsetZModifier.get();
		}
		
		@Override
		public CrosshairVisibility getCrosshairVisibility(Perspective perspective)
		{
			return this.crosshairVisibility.get(perspective).get();
		}
		
		@Override
		public boolean useCustomRaytraceDistance()
		{
			return this.useCustomRaytraceDistance.get();
		}
		
		@Override
		public double keepCameraOutOfHeadMultiplier()
		{
			return this.keepCameraOutOfHeadMultiplier.get();
		}
		
		@Override
		public boolean replaceDefaultPerspective()
		{
			return this.replaceDefaultPerspective.get();
		}
		
		@Override
		public boolean isFirstPersonEnabled()
		{
			return this.isFirstPersonEnabled.get();
		}
		
		@Override
		public boolean isThirdPersonFrontEnabled()
		{
			return this.isThirdPersonFrontEnabled.get();
		}
		
		@Override
		public boolean isThirdPersonBackEnabled()
		{
			return this.isThirdPersonBackEnabled.get();
		}
		
		@Override
		public Perspective getDefaultPerspective()
		{
			return this.defaultPerspective.get();
		}
		
		public void setDefaultPerspective(Perspective perspective)
		{
			Config.set(this.defaultPerspective, perspective);
		}
		
		@Override
		public CrosshairType getCrosshairType()
		{
			return this.crosshairType.get();
		}
		
		@Override
		public boolean doRememberLastPerspective()
		{
			return this.rememberLastPerspective.get();
		}
		
		@Override
		public double getCameraStepSize()
		{
			return this.cameraStepSize.get();
		}
		
		@Override
		public double getCameraTransitionSpeedMultiplier()
		{
			return this.cameraTransitionSpeedMultiplier.get();
		}
		
		@Override
		public double getCenterCameraWhenLookingDownAngle()
		{
			return this.centerCameraWhenLookingDownAngle.get();
		}
		
		@Override
		public double getHidePlayerWhenLookingUpAngle()
		{
			return this.hidePlayerWhenLookingUpAngle.get();
		}
		
		@Override
		public boolean doDynamicallyAdjustOffsets()
		{
			return this.dynamicallyAdjustOffsets.get();
		}
		
		@Override
		public boolean isPlayerTransparencyEnabled()
		{
			return this.playerTransparency.get();
		}
		
		@Override
		public TurningMode getTurningModeWhenUsingItem()
		{
			return this.turningModeWhenUsingItem.get();
		}
		
		@Override
		public TurningMode getTurningModeWhenAttacking()
		{
			return this.turningModeWhenAttacking.get();
		}
		
		@Override
		public TurningMode getTurningModeWhenInteracting()
		{
			return this.turningModeWhenInteraction.get();
		}
		
		@Override
		public TurningMode getTurningModeWhenPicking()
		{
			return this.turningModeWhenPicking.get();
		}
		
		@Override
		public int getTurningLockTime()
		{
			return this.turningLockTime.get();
		}
		
		@Override
		public PickOrigin getEntityPickOrigin()
		{
			return this.entityPickOrigin.get();
		}
		
		@Override
		public PickOrigin getBlockPickOrigin()
		{
			return this.blockPickOrigin.get();
		}
		
		@Override
		public PickVector getPickVector()
		{
			return this.pickVector.get();
		}
		
		@Override
		public boolean isCameraDecoupled()
		{
			return this.isCameraDecoupled.get();
		}
		
		@Override
		public double getCustomRaytraceDistance()
		{
			return this.customRaytraceDistance.get();
		}
		
		@Override
		public List<? extends String> getAdaptiveCrosshairHoldItems()
		{
			return this.adaptiveCrosshairHoldItems.get();
		}
		
		@Override
		public List<? extends String> getAdaptiveCrosshairUseItems()
		{
			return this.adaptiveCrosshairUseItems.get();
		}
		
		@Override
		public List<? extends String> getAdaptiveCrosshairHoldItemProperties()
		{
			return this.adaptiveCrosshairHoldItemProperties.get();
		}
		
		@Override
		public List<? extends String> getAdaptiveCrosshairUseItemProperties()
		{
			return this.adaptiveCrosshairUseItemProperties.get();
		}
		
		@Override
		public boolean getShowObstructionCrosshair()
		{
			return this.showObstructionIndicator.get();
		}
		
		@Override
		public boolean showObstructionIndicatorWhenAiming()
		{
			return this.showObstructionIndicatorWhenAiming.get();
		}
		
		@Override
		public double getObstructionIndicatorMaxDistanceToObstruction()
		{
			return this.obstructionIndicatorMaxDistanceToObstruction.get();
		}
		
		@Override
		public int getObstructionIndicatorMinDistanceToCrosshair()
		{
			return this.obstructionIndicatorMinDistanceToCrosshair.get();
		}
		
		@Override
		public boolean doCenterPlayerSounds()
		{
			return this.centerPlayerSounds.get();
		}
		
		@Override
		public boolean shouldPlayerXRotFollowCamera()
		{
			return this.playerXRotFollowsCamera.get();
		}
		
		@Override
		public boolean shouldPlayerYRotFollowCamera()
		{
			return this.playerYRotFollowsCamera.get();
		}
		
		@Override
		public double getPlayerYRotFollowAngleLimit()
		{
			return this.playerYRotFollowAngleLimit.get();
		}
		
		public void adjustCameraLeft()
		{
			Config.set(this.offsetX, this.addStep(this.getOffsetX(), this.getMaxOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraRight()
		{
			Config.set(this.offsetX, this.subStep(this.getOffsetX(), this.getMinOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraUp()
		{
			Config.set(this.offsetY, this.addStep(this.getOffsetY(), this.getMaxOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraDown()
		{
			Config.set(this.offsetY, this.subStep(this.getOffsetY(), this.getMinOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraIn()
		{
			Config.set(this.offsetZ, this.subStep(this.getOffsetZ(), this.getMinOffsetZ(), this.isUnlimitedOffsetZ()));
		}
		
		public void adjustCameraOut()
		{
			Config.set(this.offsetZ, this.addStep(this.getOffsetZ(), this.getMaxOffsetZ(), this.isUnlimitedOffsetZ()));
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
			Config.set(this.offsetX, -this.getOffsetX());
		}
		
		public void toggleCameraCoupling()
		{
			Config.set(this.isCameraDecoupled, !this.isCameraDecoupled());
		}
	}
	
	protected static <T> void set(ForgeConfigSpec.ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			configValue.set(value);
		}
	}
	
	public static void onConfigReload()
	{
		Perspective currentPerspective = Perspective.current();
		
		if(!currentPerspective.isEnabled(Config.CLIENT))
		{
			ShoulderSurfingImpl.getInstance().changePerspective(currentPerspective.next(Config.CLIENT));
		}
		
		if(Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(Perspective.current());
		}
	}
}
