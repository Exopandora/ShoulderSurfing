package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.config.ICrosshairConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class CrosshairConfig implements ICrosshairConfig {
	private final ConfigValue<CrosshairType> crosshairType;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItems;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItems;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemProperties;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemProperties;
	private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
	
	private final BooleanValue isObstructionIndicatorEnabled;
	private final BooleanValue isObstructionIndicatorOnlyShownWhenAiming;
	private final IntValue obstructionIndicatorMinDistanceToCrosshair;
	private final DoubleValue obstructionIndicatorMaxDistanceToObstruction;
	
	public CrosshairConfig(ModConfigSpec.Builder builder) {
		builder.push("crosshair");
		
		this.crosshairType = builder
			.comment("Crosshair type to use for shoulder surfing.")
			.translation(MOD_ID + ".configuration.crosshair.crosshair_type")
			.defineEnum("crosshair_type", CrosshairType.STATIC, CrosshairType.values());
		
		this.adaptiveCrosshairHoldItems = builder
			.comment("Items that when held, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. Example: 'minecraft:.*sword' matches 'minecraft:wooden_sword' and 'minecraft:netherite_sword'.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_items")
			.defineList("adaptive_crosshair_hold_items", () -> {
				List<String> items = new ArrayList<String>();
				items.add(BuiltInRegistries.ITEM.getKey(Items.SNOWBALL).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.EGG).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.EXPERIENCE_BOTTLE).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.ENDER_PEARL).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.SPLASH_POTION).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.FISHING_ROD).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.LINGERING_POTION).toString());
				items.add(BuiltInRegistries.ITEM.getKey(Items.WIND_CHARGE).toString());
				return items;
			}, String::new, Objects::nonNull);
		
		this.adaptiveCrosshairUseItems = builder
			.comment("Items that when used, trigger the dynamic crosshair in adaptive mode. This config option supports regular expressions. Example: 'minecraft:.*sword' matches 'minecraft:wooden_sword' and 'minecraft:netherite_sword'.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_items")
			.defineList("adaptive_crosshair_use_items", ArrayList::new, String::new, Objects::nonNull);
		
		this.adaptiveCrosshairHoldItemProperties = builder
			.comment("Item properties of an item, that when held, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_item_properties")
			.defineList("adaptive_crosshair_hold_item_properties", () -> {
				List<String> items = new ArrayList<String>();
				items.add(ResourceLocation.withDefaultNamespace("charged").toString());
				return items;
			}, String::new, item -> item != null && ResourceLocation.tryParse(item.toString()) != null);
		
		this.adaptiveCrosshairUseItemProperties = builder
			.comment("Item properties of an item, that when used, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_item_properties")
			.defineList("adaptive_crosshair_use_item_properties", () -> {
				List<String> items = new ArrayList<String>();
				items.add(ResourceLocation.withDefaultNamespace("pull").toString());
				items.add(ResourceLocation.withDefaultNamespace("throwing").toString());
				return items;
			}, String::new, item -> item != null && ResourceLocation.tryParse(item.toString()) != null);
		
		builder.push("obstruction");
		
		this.isObstructionIndicatorEnabled = builder
			.comment("When the crosshair type is static, shows an additional indicator on obstacles that stand between you and your target.")
			.translation(MOD_ID + ".configuration.obstruction.obstruction_indicator")
			.define("obstruction_indicator", true);
		
		this.isObstructionIndicatorOnlyShownWhenAiming = builder
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
		
		for (Perspective entry : Perspective.values()) {
			String key = entry.toString().toLowerCase();
			ConfigValue<CrosshairVisibility> crosshairVisibility = builder
				.comment("Crosshair visibility for " + key.replace('_', ' ') + ".")
				.translation(MOD_ID + ".configuration.crosshair.visibility." + key)
				.defineEnum(entry.toString().toLowerCase(), entry.getDefaultCrosshairVisibility(), CrosshairVisibility.values());
			this.crosshairVisibility.put(entry, crosshairVisibility);
		}
		
		builder.pop();
		builder.pop();
	}
	
	@Override
	public CrosshairVisibility getCrosshairVisibility(Perspective perspective) {
		return this.crosshairVisibility.get(perspective).get();
	}
	
	@Override
	public CrosshairType getCrosshairType() {
		return this.crosshairType.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItems() {
		return this.adaptiveCrosshairHoldItems.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItems() {
		return this.adaptiveCrosshairUseItems.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItemProperties() {
		return this.adaptiveCrosshairHoldItemProperties.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItemProperties() {
		return this.adaptiveCrosshairUseItemProperties.get();
	}
	
	@Override
	public boolean isObstructionIndicatorEnabled() {
		return this.isObstructionIndicatorEnabled.get();
	}
	
	@Override
	public boolean isObstructionIndicatorOnlyShownWhenAiming() {
		return this.isObstructionIndicatorOnlyShownWhenAiming.get();
	}
	
	@Override
	public double getObstructionIndicatorMaxDistanceToObstruction() {
		return this.obstructionIndicatorMaxDistanceToObstruction.get();
	}
	
	@Override
	public int getObstructionIndicatorMinDistanceToCrosshair() {
		return this.obstructionIndicatorMinDistanceToCrosshair.get();
	}
}
