package com.github.exopandora.shouldersurfing.neoforge.compat.curios.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.compat.curios.event.handler.ICuriosEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CuriosEventHandler implements ICuriosEventHandler {
	@Override
	public void handle(ComputePlayerAimStateEvent event) {
		var optionalInventory = CuriosApi.getCuriosInventory(event.getEntity());
		if (optionalInventory.isEmpty()) {
			return;
		}
		var inventory = optionalInventory.get();
		var integrationsConfig = Config.CLIENT.getIntegrationsConfig();
		var slotToItems = parseSlots(integrationsConfig.getCuriosAdaptiveCrosshairItems());
		var slotToDefaultItemComponents = parseSlots(
			integrationsConfig.getCuriosAdaptiveCrosshairDefaultItemComponents()
		);
		var slotToItemComponents = parseSlots(
			integrationsConfig.getCuriosAdaptiveCrosshairItemComponents()
		);
		for (var entry : inventory.getCurios().entrySet()) {
			var items = slotToItems.getOrDefault(entry.getKey(), Collections.emptyList());
			var defaultComponentIds = slotToDefaultItemComponents.getOrDefault(
				entry.getKey(), Collections.emptyList()
			);
			var componentIds = slotToItemComponents.getOrDefault(entry.getKey(), Collections.emptyList());
			if (items.isEmpty() && defaultComponentIds.isEmpty() && componentIds.isEmpty()) {
				continue;
			}
			var stackHandler = entry.getValue().getStacks();
			for (var x = 0; x < stackHandler.getSlots(); x++) {
				var stack = stackHandler.getStackInSlot(x);
				var isAdaptiveItemStack = ComputePlayerAimStateEventHandlerImpl.isAdaptiveItemStack(
					stack, items, componentIds, defaultComponentIds, Collections.emptyList()
				);
				if (isAdaptiveItemStack) {
					event.setResult(true);
					return;
				}
			}
		}
	}
	
	private static Map<String, List<String>> parseSlots(List<? extends String> list) {
		var result = new HashMap<String, List<String>>();
		for (var element : list) {
			var split = element.split("@", 2);
			result.computeIfAbsent(split[0], _ -> new LinkedList<String>()).add(split[1]);
		}
		return result;
	}
}
