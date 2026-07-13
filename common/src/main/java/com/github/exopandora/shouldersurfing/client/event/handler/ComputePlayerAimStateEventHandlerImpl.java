package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum ComputePlayerAimStateEventHandlerImpl implements ComputePlayerAimStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputePlayerAimStateEvent event) {
		var useStack = event.getEntity().getUseItem();
		var crosshairConfig = Config.CLIENT.getCrosshairConfig();
		var useItems = crosshairConfig.getAdaptiveCrosshairUseItems();
		var useItemComponents = crosshairConfig.getAdaptiveCrosshairUseItemComponents();
		var useItemDefaultComponents = crosshairConfig.getAdaptiveCrosshairUseItemDefaultComponents();
		var useItemAnimations = crosshairConfig.getAdaptiveCrosshairUseItemAnimations();
		if (isAdaptiveItemStack(useStack, useItems, useItemComponents, useItemDefaultComponents, useItemAnimations)) {
			event.setResult(true);
			return;
		}
		var holdItems = crosshairConfig.getAdaptiveCrosshairHoldItems();
		var holdItemComponents = crosshairConfig.getAdaptiveCrosshairHoldItemComponents();
		var holdDefaultComponents = crosshairConfig.getAdaptiveCrosshairHoldItemDefaultComponents();
		var holdItemAnimations = crosshairConfig.getAdaptiveCrosshairHoldItemAnimations();
		var handItems = new ItemStack[]{event.getEntity().getMainHandItem(), event.getEntity().getOffhandItem()};
		for (var handStack : handItems) {
			if (isAdaptiveItemStack(handStack, holdItems, holdItemComponents, holdDefaultComponents, holdItemAnimations)) {
				event.setResult(true);
				return;
			}
		}
	}
	
	public static boolean isAdaptiveItemStack(
		ItemStack stack,
		List<? extends String> expressions,
		List<? extends String> componentIds,
		List<? extends String> defaultComponentIds,
		List<? extends String> itemAnimations
	) {
		var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		if (expressions.stream().map(ComputePlayerAimStateEventHandlerImpl::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId))) {
			return true;
		}
		if (!stack.getComponentsPatch().isEmpty()) {
			var patch = stack.getComponentsPatch();
			for (var componentId : componentIds) {
				var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(componentId));
				if (type.isEmpty()) {
					continue;
				}
				var component = patch.get(stack, type.get());
				if (component != null) {
					return true;
				}
			}
		}
		if (!stack.getComponents().isEmpty()) {
			var components = stack.getComponents();
			for (var defaultComponentId : defaultComponentIds) {
				var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(defaultComponentId));
				if (type.isEmpty()) {
					continue;
				}
				if (components.get(type.get()) != null) {
					return true;
				}
			}
		}
		var useAnimation = stack.getUseAnimation().getSerializedName();
		for (var itemAnimation : itemAnimations) {
			if (itemAnimation.equals(useAnimation)) {
				return true;
			}
		}
		return false;
	}
	
	private static Predicate<String> expressionToMatchPredicate(String expression) {
		try {
			return Pattern.compile(expression).asMatchPredicate();
		} catch (Exception e) {
			return expression::equals;
		}
	}
}
