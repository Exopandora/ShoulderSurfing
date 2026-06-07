package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.IIntegrationsConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

import java.util.ArrayList;
import java.util.List;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class IntegrationsConfig implements IIntegrationsConfig
{
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItems;
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairDefaultItemComponents;
	private final ConfigValue<List<? extends String>> curiosAdaptiveCrosshairItemComponents;
	
	protected IntegrationsConfig(ModConfigSpec.Builder builder)
	{
		builder.push("integrations");
		builder.push("curios");
		
		this.curiosAdaptiveCrosshairItems = builder
			.comment("Items that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. The curios slot must be specified before the expression and is separated by an '@' character. Example: 'ring@angelring:.*_ring' matches 'angelring:diamond_ring' and 'angelring:angel_ring' when equipped in the 'ring' slot.")
			.translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_items")
			.defineList("adaptive_crosshair_items", ArrayList::new, String::new, Config.ClientConfig::isValidItemWithSlot);
		
		this.curiosAdaptiveCrosshairItemComponents = builder
			.comment("Item components (modified only) of an item, that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. Example: 'necklace@consumable'")
			.translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_item_components")
			.defineList("adaptive_crosshair_item_components", ArrayList::new, String::new, Config.ClientConfig::isValidDataComponentIdWithSlot);
		
		this.curiosAdaptiveCrosshairDefaultItemComponents = builder
			.comment("Default components of an item, that when equipped in a curios slot, trigger the dynamic crosshair in adaptive mode. Example: 'necklace@consumable'")
			.translation(MOD_ID + ".configuration.integrations.curios.adaptive_crosshair_default_item_components")
			.defineList("adaptive_crosshair_item_default_components", ArrayList::new, String::new, Config.ClientConfig::isValidDataComponentIdWithSlot);
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItems()
	{
		return this.curiosAdaptiveCrosshairItems.get();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairDefaultItemComponents()
	{
		return this.curiosAdaptiveCrosshairDefaultItemComponents.get();
	}
	
	@Override
	public List<? extends String> getCuriosAdaptiveCrosshairItemComponents()
	{
		return this.curiosAdaptiveCrosshairItemComponents.get();
	}
}
