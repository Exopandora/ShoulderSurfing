package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.config.IPerspectiveConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class PerspectiveConfig implements IPerspectiveConfig {
	private final BooleanValue isThirdPersonReplaced;
	private final BooleanValue isFirstPersonEnabled;
	private final BooleanValue isThirdPersonFrontEnabled;
	private final BooleanValue isThirdPersonBackEnabled;
	private final ConfigValue<Perspective> defaultPerspective;
	private final BooleanValue isPerspectivePersistent;
	
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
}
