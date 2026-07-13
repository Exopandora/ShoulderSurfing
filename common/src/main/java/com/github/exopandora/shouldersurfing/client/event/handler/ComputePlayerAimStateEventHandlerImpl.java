package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputePlayerAimStateEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
		var useItemProperties = crosshairConfig.getAdaptiveCrosshairUseItemProperties();
		if (isAdaptiveItemStack(useStack, useItems, useItemProperties)) {
			event.setResult(true);
			return;
		}
		var holdItems = crosshairConfig.getAdaptiveCrosshairHoldItems();
		var holdItemProperties = crosshairConfig.getAdaptiveCrosshairHoldItemProperties();
		var handItems = new ItemStack[]{event.getEntity().getMainHandItem(), event.getEntity().getOffhandItem()};
		for (var handStack : handItems) {
			if (isAdaptiveItemStack(handStack, holdItems, holdItemProperties)) {
				event.setResult(true);
				return;
			}
		}
	}
	
	public static boolean isAdaptiveItemStack(ItemStack stack, List<? extends String> expressions, List<? extends String> itemProperties) {
		var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		if (expressions.stream().map(ComputePlayerAimStateEventHandlerImpl::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId))) {
			return true;
		}
		for (var itemProperty : itemProperties) {
			if (ItemProperties.getProperty(stack, ResourceLocation.parse(itemProperty)) != null) {
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
