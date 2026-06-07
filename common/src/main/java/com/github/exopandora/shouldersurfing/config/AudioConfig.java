package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.IAudioConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class AudioConfig implements IAudioConfig
{
	private final BooleanValue centerPlayerSounds;
	
	protected AudioConfig(ModConfigSpec.Builder builder)
	{
		builder.push("audio");
		
		this.centerPlayerSounds = builder
			.comment("Whether to center sounds made by the player.")
			.translation(MOD_ID + ".configuration.audio.center_player_sounds")
			.define("center_player_sounds", false);
		
		builder.pop();
	}
	
	@Override
	public boolean doCenterPlayerSounds()
	{
		return this.centerPlayerSounds.get();
	}
}
