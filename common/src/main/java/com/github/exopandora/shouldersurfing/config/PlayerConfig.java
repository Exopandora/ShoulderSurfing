package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.TurningMode;
import com.github.exopandora.shouldersurfing.api.config.IPlayerConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class PlayerConfig implements IPlayerConfig {
	private final BooleanValue playerTransparency;
	private final BooleanValue isPlayerTransparentWhenAiming;
	private final DoubleValue hidePlayerWhenLookingUpAngle;
	private final ConfigValue<TurningMode> turningModeWhenUsingItem;
	private final ConfigValue<TurningMode> turningModeWhenAttacking;
	private final ConfigValue<TurningMode> turningModeWhenInteraction;
	private final ConfigValue<TurningMode> turningModeWhenPicking;
	private final IntValue turningLockTime;
	private final DoubleValue turningSpeedMultiplier;
	private final BooleanValue isPlayerXRotTurningWithCamera;
	private final BooleanValue isPlayerYRotTurningWithCamera;
	private final DoubleValue playerYRotTurnAngleLimit;
	
	protected PlayerConfig(ModConfigSpec.Builder builder) {
		builder.push("player");
		
		this.playerTransparency = builder
			.comment("Whether to adjust the player model transparency when view is obstructed. Changing this value may require a game restart to take full effect.")
			.translation(MOD_ID + ".configuration.player.adjust_player_transparency")
			.gameRestart()
			.define("adjust_player_transparency", true);
		
		this.isPlayerTransparentWhenAiming = builder
			.comment("Whether to turn the player model transparent when aiming. This config option only applies when adjust player transparency is enabled.")
			.translation(MOD_ID + ".configuration.player.turn_player_transparent_when_aiming")
			.define("turn_player_transparent_when_aiming", false);
		
		this.hidePlayerWhenLookingUpAngle = builder
			.comment("The angle at which the player will no longer be rendered when looking up. Set to 0 to disable.")
			.translation(MOD_ID + ".configuration.player.hide_player_when_looking_up_angle")
			.defineInRange("hide_player_when_looking_up_angle", 0D, 0D, 90D);
		
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
		
		this.turningSpeedMultiplier = builder
			.comment("The speed multiplier at which the player rotation converges to the movement direction of the player.")
			.translation(MOD_ID + ".configuration.player.turning.turning_speed_multiplier")
			.defineInRange("turning_speed_multiplier", 0.25F, 0.05F, 1.0F);
		
		this.isPlayerXRotTurningWithCamera = builder
			.comment("Whether the x-rot of the player turns with the camera. This config option only applies when camera is decoupled.")
			.translation(MOD_ID + ".configuration.player.turning.turn_player_x_rot_with_camera")
			.define("turn_player_x_rot_with_camera", true);
		
		this.isPlayerYRotTurningWithCamera = builder
			.comment("Whether the y-rot of the player turns with the camera. This config option only applies when camera is decoupled.")
			.translation(MOD_ID + ".configuration.player.turning.turn_player_y_rot_with_camera")
			.define("turn_player_y_rot_with_camera", true);
		
		this.playerYRotTurnAngleLimit = builder
			.comment("The maximum angle to turn the player y-rot with the camera. This config option only applies when 'Turn player y-rot with camera' is enabled.")
			.translation(MOD_ID + ".configuration.player.turning.turn_player_y_rot_angle_limit")
			.defineInRange("turn_player_y_rot_angle_limit", 90D, 0D, 180D);
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public double getHidePlayerWhenLookingUpAngle() {
		return this.hidePlayerWhenLookingUpAngle.get();
	}
	
	@Override
	public boolean isPlayerTransparencyEnabled() {
		return Config.CLIENT_SPEC.isLoaded() ? this.playerTransparency.get() : this.playerTransparency.getDefault();
	}
	
	@Override
	public boolean isPlayerTransparentWhenAiming() {
		return this.isPlayerTransparentWhenAiming.get();
	}
	
	@Override
	public TurningMode getTurningModeWhenUsingItem() {
		return this.turningModeWhenUsingItem.get();
	}
	
	@Override
	public TurningMode getTurningModeWhenAttacking() {
		return this.turningModeWhenAttacking.get();
	}
	
	@Override
	public TurningMode getTurningModeWhenInteracting() {
		return this.turningModeWhenInteraction.get();
	}
	
	@Override
	public TurningMode getTurningModeWhenPicking() {
		return this.turningModeWhenPicking.get();
	}
	
	@Override
	public int getTurningLockTime() {
		return this.turningLockTime.get();
	}
	
	@Override
	public double getTurningSpeedMultiplier() {
		return this.turningSpeedMultiplier.get();
	}
	
	@Override
	public boolean isPlayerXRotTurningWithCamera() {
		return this.isPlayerXRotTurningWithCamera.get();
	}
	
	@Override
	public boolean isPlayerYRotTurningWithCamera() {
		return this.isPlayerYRotTurningWithCamera.get();
	}
	
	@Override
	public double getPlayerYRotTurnAngleLimit() {
		return this.playerYRotTurnAngleLimit.get();
	}
}
