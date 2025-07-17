package com.github.exopandora.shouldersurfing.neoforge.compat.plugin;

import com.github.exopandora.shouldersurfing.compat.plugin.ICuriosAdaptiveItemCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.callbacks.AdaptiveItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
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

public class CuriosAdaptiveItemCallback implements ICuriosAdaptiveItemCallback
{
	@Override
	public boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		Optional<ICuriosItemHandler> optionalInventory = CuriosApi.getCuriosInventory(entity);
		
		if(optionalInventory.isEmpty())
		{
			return false;
		}
		
		ICuriosItemHandler inventory = optionalInventory.get();
		Map<String, List<String>> slotToItems = parseSlots(Config.CLIENT.getCuriosAdaptiveCrosshairItems());
		Map<String, List<String>> slotToItemProperties = parseSlots(Config.CLIENT.getCuriosAdaptiveCrosshairItemProperties());
		
		for(Entry<String, ICurioStacksHandler> entry : inventory.getCurios().entrySet())
		{
			List<String> items = slotToItems.getOrDefault(entry.getKey(), Collections.emptyList());
			List<String> itemProperties = slotToItemProperties.getOrDefault(entry.getKey(), Collections.emptyList());
			
			if(items.isEmpty() && itemProperties.isEmpty())
			{
				continue;
			}
			
			IDynamicStackHandler stackHandler = entry.getValue().getStacks();
			
			for(int x = 0; x < stackHandler.getSlots(); x++)
			{
				ItemStack stack = stackHandler.getStackInSlot(x);
				
				if(AdaptiveItemCallback.isAdaptiveItemStack(stack, items, itemProperties))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static Map<String, List<String>> parseSlots(List<? extends String> list)
	{
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		
		for(String element : list)
		{
			String[] split = element.split("@", 2);
			result.computeIfAbsent(split[0], key -> new LinkedList<String>()).add(split[1]);
		}
		
		return result;
	}
}
