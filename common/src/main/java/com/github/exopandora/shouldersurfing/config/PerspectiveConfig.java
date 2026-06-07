package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.IPerspectiveConfig;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class PerspectiveConfig implements IPerspectiveConfig
{
	private final BooleanValue replaceDefaultPerspective;
	private final BooleanValue isFirstPersonEnabled;
	private final BooleanValue isThirdPersonFrontEnabled;
	private final BooleanValue isThirdPersonBackEnabled;
	private final ConfigValue<Perspective> defaultPerspective;
	private final BooleanValue rememberLastPerspective;
	
	protected PerspectiveConfig(ModConfigSpec.Builder builder)
	{
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
		Config.CLIENT.set(this.defaultPerspective, perspective);
	}
	
	@Override
	public boolean doRememberLastPerspective()
	{
		return this.rememberLastPerspective.get();
	}
}
