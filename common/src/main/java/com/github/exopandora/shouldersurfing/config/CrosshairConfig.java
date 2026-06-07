package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.ICrosshairConfig;
import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.CrosshairVisibility;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemUseAnimation;
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
	private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemAnimations;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemAnimations;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemDefaultComponents;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemDefaultComponents;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairHoldItemComponents;
	private final ConfigValue<List<? extends String>> adaptiveCrosshairUseItemComponents;
	private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
	
	private final BooleanValue showObstructionIndicator;
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
		
		this.adaptiveCrosshairHoldItemAnimations = builder
			.comment("Item use-animations of an item, that when the item is held, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_item_animations")
			.defineList("adaptive_crosshair_hold_item_animations", ArrayList::new, String::new, Config.ClientConfig::isValidItemUseAnimation);
		
		this.adaptiveCrosshairUseItemAnimations = builder
			.comment("Item use-animations of an item, that when the item is used, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_item_animations")
			.defineList("adaptive_crosshair_use_item_animations", () -> {
				List<String> items = new ArrayList<String>();
				items.add(ItemUseAnimation.BOW.getSerializedName());
				items.add(ItemUseAnimation.TRIDENT.getSerializedName());
				return items;
			}, String::new, Config.ClientConfig::isValidItemUseAnimation);
		
		this.adaptiveCrosshairHoldItemComponents = builder
			.comment("Item components (modified only) of an item, that when the item is held, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_hold_item_components")
			.defineList("adaptive_crosshair_hold_item_components", () -> {
				List<String> components = new ArrayList<String>();
				components.add(Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.CHARGED_PROJECTILES)).toString());
				components.add(Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.PIERCING_WEAPON)).toString());
				return components;
			}, String::new, Config.ClientConfig::isValidDataComponentId);
		
		this.adaptiveCrosshairUseItemComponents = builder
			.comment("Item components (modified only) of an item, that when the item is used, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_use_item_components")
			.defineList("adaptive_crosshair_use_item_components", ArrayList::new, String::new, Config.ClientConfig::isValidDataComponentId);
		
		this.adaptiveCrosshairHoldItemDefaultComponents = builder
			.comment("Default components of an item, that when the item is held, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_item_hold_default_components")
			.defineList("adaptive_crosshair_item_hold_default_components", () -> {
				List<String> components = new ArrayList<String>();
				components.add(Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.PIERCING_WEAPON)).toString());
				return components;
			}, String::new, Config.ClientConfig::isValidDataComponentId);
		
		this.adaptiveCrosshairUseItemDefaultComponents = builder
			.comment("Default components of an item, that when the item is used, trigger the dynamic crosshair in adaptive mode.")
			.translation(MOD_ID + ".configuration.crosshair.adaptive_crosshair_item_use_default_components")
			.defineList("adaptive_crosshair_item_use_default_components", ArrayList::new, String::new, Config.ClientConfig::isValidDataComponentId);
		
		builder.push("obstruction");
		
		this.showObstructionIndicator = builder
			.comment("When the crosshair type is static, shows an additional indicator on obstacles that stand between you and your target.")
			.translation(MOD_ID + ".configuration.obstruction.show_obstruction_indicator")
			.define("show_obstruction_indicator", true);
		
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
	public List<? extends String> getAdaptiveCrosshairHoldItemAnimations() {
		return this.adaptiveCrosshairHoldItemAnimations.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItemAnimations() {
		return this.adaptiveCrosshairUseItemAnimations.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItemDefaultComponents() {
		return this.adaptiveCrosshairHoldItemDefaultComponents.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItemDefaultComponents() {
		return this.adaptiveCrosshairUseItemDefaultComponents.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairHoldItemComponents() {
		return this.adaptiveCrosshairHoldItemComponents.get();
	}
	
	@Override
	public List<? extends String> getAdaptiveCrosshairUseItemComponents() {
		return this.adaptiveCrosshairUseItemComponents.get();
	}
	
	@Override
	public boolean getShowObstructionCrosshair() {
		return this.showObstructionIndicator.get();
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
