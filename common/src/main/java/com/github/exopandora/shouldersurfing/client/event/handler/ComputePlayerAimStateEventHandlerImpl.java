package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairConfig;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum ComputePlayerAimStateEventHandlerImpl implements ComputePlayerAimStateEventHandler {
	INSTANCE;
	
	@Override
	public void handle(ComputePlayerAimStateEvent event) {
		ItemStack useStack = event.getEntity().getUseItem();
		CrosshairConfig crosshairConfig = Config.CLIENT.getCrosshairConfig();
		List<? extends String> useItems = crosshairConfig.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemComponents = crosshairConfig.getAdaptiveCrosshairUseItemComponents();
		List<? extends String> useItemDefaultComponents = crosshairConfig.getAdaptiveCrosshairUseItemDefaultComponents();
		List<? extends String> useItemAnimations = crosshairConfig.getAdaptiveCrosshairUseItemAnimations();
		if (isAdaptiveItemStack(useStack, useItems, useItemComponents, useItemDefaultComponents, useItemAnimations)) {
			event.setResult(true);
			return;
		}
		List<? extends String> holdItems = crosshairConfig.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemComponents = crosshairConfig.getAdaptiveCrosshairHoldItemComponents();
		List<? extends String> holdDefaultComponents = crosshairConfig.getAdaptiveCrosshairHoldItemDefaultComponents();
		List<? extends String> holdItemAnimations = crosshairConfig.getAdaptiveCrosshairHoldItemAnimations();
		ItemStack[] handItems = {event.getEntity().getMainHandItem(), event.getEntity().getOffhandItem()};
		for (ItemStack handStack : handItems) {
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
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		if (expressions.stream().map(ComputePlayerAimStateEventHandlerImpl::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId))) {
			return true;
		}
		if (!stack.getComponentsPatch().isEmpty()) {
			DataComponentPatch patch = stack.getComponentsPatch();
			for (String componentId : componentIds) {
				Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(componentId));
				if (type.isEmpty()) {
					continue;
				}
				Object component = patch.get(stack, type.get());
				if (component != null) {
					return true;
				}
			}
		}
		if (!stack.getComponents().isEmpty()) {
			DataComponentMap components = stack.getComponents();
			for (String defaultComponentId : defaultComponentIds) {
				Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(defaultComponentId));
				if (type.isEmpty()) {
					continue;
				}
				if (components.get(type.get()) != null) {
					return true;
				}
			}
		}
		String useAnimation = stack.getUseAnimation().getSerializedName();
		for (String itemAnimation : itemAnimations) {
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
