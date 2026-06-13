package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.config.IPerspectiveConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class PerspectiveConfig implements IPerspectiveConfig {
	private final BooleanValue isThirdPersonReplaced;
	private final BooleanValue isFirstPersonEnabled;
	private final BooleanValue isThirdPersonFrontEnabled;
	private final BooleanValue isThirdPersonBackEnabled;
	private final ConfigValue<Perspective> defaultPerspective;
	private final BooleanValue isPerspectivePersistent;
	private final BooleanValue isTemporaryFirstPersonInConstrainedSpacesEnabled;
	private final IntValue temporaryFirstPersonInConstrainedSpacesCooldown;
	private final DoubleValue temporaryFirstPersonOffsetXThreshold;
	private final DoubleValue temporaryFirstPersonOffsetYThreshold;
	private final DoubleValue temporaryFirstPersonOffsetZThreshold;
	
	protected PerspectiveConfig(ModConfigSpec.Builder builder) {
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
		
		this.isTemporaryFirstPersonInConstrainedSpacesEnabled = builder
			.comment("Whether to switch to first person temporarily when space constrained.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces")
			.define("temporary_first_person_in_constrained_spaces", false);
		
		this.temporaryFirstPersonInConstrainedSpacesCooldown = builder
			.comment("The time in ticks the temporary first person perspective will be enabled after leaving a constrained space.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_in_constrained_spaces_cooldown")
			.defineInRange("temporary_first_person_in_constrained_spaces_cooldown", 10, 0, Integer.MAX_VALUE);
		
		builder.push("temporary_first_person_offset_threshold");
		
		this.temporaryFirstPersonOffsetXThreshold = builder
			.comment("Temporary first person x-offset threshold.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_offset_threshold.offset_x")
			.defineInRange("offset_x", 0.5D, 0, Double.MAX_VALUE);
		
		this.temporaryFirstPersonOffsetYThreshold = builder
			.comment("Temporary first person y-offset threshold.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_offset_threshold.offset_y")
			.defineInRange("offset_y", 0.0D, 0, Double.MAX_VALUE);
		
		this.temporaryFirstPersonOffsetZThreshold = builder
			.comment("Temporary first person z-offset threshold.")
			.translation(MOD_ID + ".configuration.perspective.temporary_first_person_offset_threshold.offset_z")
			.defineInRange("offset_z", 0.5D, 0, Double.MAX_VALUE);
		
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
	public int getTemporaryFirstPersonInConstrainedSpacesCooldown() {
		return this.temporaryFirstPersonInConstrainedSpacesCooldown.get();
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
