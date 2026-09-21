package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.config.IPerspectiveConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class PerspectiveConfig implements IPerspectiveConfig {
	private final BooleanValue isThirdPersonReplaced;
	private final BooleanValue isFirstPersonEnabled;
	private final BooleanValue isThirdPersonFrontEnabled;
	private final BooleanValue isThirdPersonBackEnabled;
	private final ConfigValue<Perspective> defaultPerspective;
	private final BooleanValue isPerspectivePersistent;
	private final BooleanValue isTemporaryFirstPersonInConstrainedSpacesEnabled;
	private final IntValue temporaryFirstPersonInConstrainedSpacesMinimumTime;
	private final IntValue temporaryFirstPersonInConstrainedSpacesAdditionalTime;
	private final IntValue temporaryFirstPersonInConstrainedSpacesCooldownTime;
	private final DoubleValue temporaryFirstPersonOffsetXThreshold;
	private final DoubleValue temporaryFirstPersonOffsetYThreshold;
	private final DoubleValue temporaryFirstPersonOffsetZThreshold;
	
	protected PerspectiveConfig(ForgeConfigSpec.Builder builder) {
		builder.push("perspective");
		
		this.defaultPerspective = builder
			.comment("The default perspective when you load the game.")
			.translation(MOD_ID + ".configuration.perspective.default_perspective")
			.defineEnum("default_perspective", Perspective.SHOULDER_SURFING, Perspective.values());
		
		this.isPerspectivePersistent = builder
			.comment("Whether to remember the last perspective used.")
			.translation(MOD_ID + ".configuration.perspective.remember_last_perspective")
			.define("remember_last_perspective", true);
		
		this.isThirdPersonReplaced = builder
			.comment("Whether to replace the default third person perspective.")
			.translation(MOD_ID + ".configuration.perspective.replace_default_perspective")
			.define("replace_default_perspective", false);
		
		this.isFirstPersonEnabled = builder
			.comment("Whether the first person perspective is enabled.")
			.translation(MOD_ID + ".configuration.perspective.first_person_enabled")
			.define("first_person_enabled", true);
		
		this.isThirdPersonFrontEnabled = builder
			.comment("Whether the third person front perspective is enabled.")
			.translation(MOD_ID + ".configuration.perspective.third_person_front_enabled")
			.define("third_person_front_enabled", true);
		
		this.isThirdPersonBackEnabled = builder
			.comment("Whether the third person back perspective is enabled.")
			.translation(MOD_ID + ".configuration.perspective.third_person_back_enabled")
			.define("third_person_back_enabled", true);
		
		builder.push("temporary_first_person_in_constrained_spaces");
		
		this.isTemporaryFirstPersonInConstrainedSpacesEnabled = builder
			.comment("Whether to switch to first person temporarily when space constrained.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.enabled")
			.define("enabled", false);
		
		this.temporaryFirstPersonInConstrainedSpacesMinimumTime = builder
			.comment("The minium time in ticks the temporary first will be active when entering a constrained space.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.minimum_time")
			.defineInRange("minimum_time", 60, 0, Integer.MAX_VALUE);
		
		this.temporaryFirstPersonInConstrainedSpacesAdditionalTime = builder
			.comment("The additional time in ticks the perspective will stay in temporary first person after leaving a constrained space.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.additional_time")
			.defineInRange("additional_time", 20, 0, Integer.MAX_VALUE);
		
		this.temporaryFirstPersonInConstrainedSpacesCooldownTime = builder
			.comment("The time in ticks the temporary first will be on cooldown before it can be entered again.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.cooldown_time")
			.defineInRange("cooldown_time", 60, 0, Integer.MAX_VALUE);
		
		builder.push("offset_threshold");
		
		this.temporaryFirstPersonOffsetXThreshold = builder
			.comment("The x-axis camera offset threshold. When the x-axis camera offset falls below the configured threshold, the perspective will switch to temporary first person.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.offset_threshold.offset_x")
			.defineInRange("offset_x", 0.1D, 0, Double.MAX_VALUE);
		
		this.temporaryFirstPersonOffsetYThreshold = builder
			.comment("The y-axis camera offset threshold. When the y-axis camera offset falls below the configured threshold, the perspective will switch to temporary first person.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.offset_threshold.offset_y")
			.defineInRange("offset_y", 0.0D, 0, Double.MAX_VALUE);
		
		this.temporaryFirstPersonOffsetZThreshold = builder
			.comment("The z-axis camera offset threshold. When the z-axis camera offset falls below the configured threshold, the perspective will switch to temporary first person.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces.offset_threshold.offset_z")
			.defineInRange("offset_z", 0.1D, 0, Double.MAX_VALUE);
		
		builder.pop();
		builder.pop();
		builder.pop();
	}
	
	@Override
	public boolean isThirdPersonReplaced() {
		return this.isThirdPersonReplaced.get();
	}
	
	@Override
	public boolean isFirstPersonEnabled() {
		return this.isFirstPersonEnabled.get();
	}
	
	@Override
	public boolean isThirdPersonFrontEnabled() {
		return this.isThirdPersonFrontEnabled.get();
	}
	
	@Override
	public boolean isThirdPersonBackEnabled() {
		return this.isThirdPersonBackEnabled.get();
	}
	
	@Override
	public Perspective getDefaultPerspective() {
		return this.defaultPerspective.get();
	}
	
	public void setDefaultPerspective(Perspective perspective) {
		Config.CLIENT.set(this.defaultPerspective, perspective);
	}
	
	@Override
	public boolean isPerspectivePersistent() {
		return this.isPerspectivePersistent.get();
	}
	
	@Override
	public boolean isTemporaryFirstPersonInConstrainedSpacesEnabled() {
		return this.isTemporaryFirstPersonInConstrainedSpacesEnabled.get();
	}
	
	@Override
	public int getTemporaryFirstPersonInConstrainedSpacesMinimumTime() {
		return this.temporaryFirstPersonInConstrainedSpacesMinimumTime.get();
	}
	
	@Override
	public int getTemporaryFirstPersonInConstrainedSpacesAdditionalTime() {
		return this.temporaryFirstPersonInConstrainedSpacesAdditionalTime.get();
	}
	
	@Override
	public int getTemporaryFirstPersonInConstrainedSpacesCooldownTime() {
		return this.temporaryFirstPersonInConstrainedSpacesCooldownTime.get();
	}
	
	@Override
	public double getTemporaryFirstPersonOffsetXThreshold() {
		return this.temporaryFirstPersonOffsetXThreshold.get();
	}
	
	@Override
	public double getTemporaryFirstPersonOffsetYThreshold() {
		return this.temporaryFirstPersonOffsetYThreshold.get();
	}
	
	@Override
	public double getTemporaryFirstPersonOffsetZThreshold() {
		return this.temporaryFirstPersonOffsetZThreshold.get();
	}
}
