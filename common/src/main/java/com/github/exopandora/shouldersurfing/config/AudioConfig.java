package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.config.IAudioConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class AudioConfig implements IAudioConfig {
	private final BooleanValue isPlayerSoundCentered;
	
	protected AudioConfig(ForgeConfigSpec.Builder builder) {
		builder.push("audio");
		
		this.isPlayerSoundCentered = builder
			.comment("Whether to center sounds made by the player.")
			.translation(MOD_ID + ".configuration.audio.center_player_sounds")
			.define("center_player_sounds", false);
		
		builder.pop();
	}
	
	@Override
	public boolean isPlayerSoundCentered() {
		return this.isPlayerSoundCentered.get();
	}
}
