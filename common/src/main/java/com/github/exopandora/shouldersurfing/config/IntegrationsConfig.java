package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.config.IIntegrationsConfig;
import com.github.exopandora.shouldersurfing.config.Config.ClientConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.ArrayList;
import java.util.List;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class IntegrationsConfig implements IIntegrationsConfig {
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItems;
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItemProperties;
	private final BooleanValue isEpicFightDecoupledCameraLockOnEnabled;
	
	protected IntegrationsConfig(ForgeConfigSpec.Builder builder) {
		builder.push("integrations");
		builder.push("curios");
		
		this.curiosAdaptiveCrosshairItems = builder
			.comment("Items that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. The curios slot must be specified before the expression and is separated by an '@' character. Example: 'ring@angelring:.*_ring' matches 'angelring:diamond_ring' and 'angelring:angel_ring' when equipped in the 'ring' slot.")
			.translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_items")
			.defineList("adaptive_crosshair_items", ArrayList::new, ClientConfig::isValidItemWithSlot);
		
		this.curiosAdaptiveCrosshairItemProperties = builder
		   .comment("Item properties of an item, that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. Example: 'necklace@charged'")
		   .translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_item_properties")
		   .defineList("adaptive_crosshair_item_properties", ArrayList::new, ClientConfig::isValidItemPropertyWithSlot);
		
		builder.pop();
		builder.push("epicfight");
		
		this.isEpicFightDecoupledCameraLockOnEnabled = builder
			.comment("Whether to allow target lock-on when camera is decoupled.")
			.translation(MOD_ID + ".configuration.integrations.epicfight.decoupled_camera_lock_on")
			.define("decoupled_camera_lock_on", false);
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItems() {
		return this.curiosAdaptiveCrosshairItems.get();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItemProperties() {
		return this.curiosAdaptiveCrosshairItemProperties.get();
	}
	
	@Override
	public boolean isEpicFightDecoupledCameraLockOnEnabled() {
		return this.isEpicFightDecoupledCameraLockOnEnabled.get();
	}
}
