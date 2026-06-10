package com.github.exopandora.shouldersurfing.neoforge.compat.curios.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.github.exopandora.shouldersurfing.compat.curios.event.handler.ICuriosEventHandler;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.IntegrationsConfig;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class CuriosEventHandler implements ICuriosEventHandler {
	@Override
	public void handle(ComputePlayerAimStateEvent event) {
		Optional<ICuriosItemHandler> optionalInventory = CuriosApi.getCuriosInventory(event.getEntity());
		if (optionalInventory.isEmpty()) {
			return;
		}
		ICuriosItemHandler inventory = optionalInventory.get();
		IntegrationsConfig integrationsConfig = Config.CLIENT.getIntegrationsConfig();
		Map<String, List<String>> slotToItems = parseSlots(integrationsConfig.getCuriosAdaptiveCrosshairItems());
		Map<String, List<String>> slotToDefaultItemComponents = parseSlots(integrationsConfig.getCuriosAdaptiveCrosshairDefaultItemComponents());
		Map<String, List<String>> slotToItemComponents = parseSlots(integrationsConfig.getCuriosAdaptiveCrosshairItemComponents());
		for (Entry<String, ICurioStacksHandler> entry : inventory.getCurios().entrySet()) {
			List<String> items = slotToItems.getOrDefault(entry.getKey(), Collections.emptyList());
			List<String> defaultComponentIds = slotToDefaultItemComponents.getOrDefault(entry.getKey(), Collections.emptyList());
			List<String> componentIds = slotToItemComponents.getOrDefault(entry.getKey(), Collections.emptyList());
			if (items.isEmpty() && defaultComponentIds.isEmpty() && componentIds.isEmpty()) {
				continue;
			}
			IDynamicStackHandler stackHandler = entry.getValue().getStacks();
			for (int x = 0; x < stackHandler.getSlots(); x++) {
				ItemStack stack = stackHandler.getStackInSlot(x);
				if (ComputePlayerAimStateEventHandlerImpl.isAdaptiveItemStack(stack, items, componentIds, defaultComponentIds, Collections.emptyList())) {
					event.setResult(true);
					return;
				}
			}
		}
	}
	
	private static Map<String, List<String>> parseSlots(List<? extends String> list) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (String element : list) {
			String[] split = element.split("@", 2);
			result.computeIfAbsent(split[0], _ -> new LinkedList<String>()).add(split[1]);
		}
		return result;
	}
}
