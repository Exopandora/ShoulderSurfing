package com.github.exopandora.shouldersurfing.api.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface ComputePlayerAimStateEventHandler {
	void handle(ComputePlayerAimStateEvent event);
	
	/**
	 * @param items The items to match
	 * @return A ComputePlayerAimStateEventHandler instance that sets the result to <code>true</code> when the main-hand matches any of the listed items
	 */
	static ComputePlayerAimStateEventHandler mainHandMatches(ItemLike... items) {
		return (event) -> {
			if (!event.getResult()) {
				event.setResult(containsItem(event.getEntity().getMainHandItem().getItem(), items));
			}
		};
	}
	
	/**
	 * @param items Items to match
	 * @return A ComputePlayerAimStateEventHandler instance that sets the result to <code>true</code> when the off-hand matches any of the listed items
	 */
	static ComputePlayerAimStateEventHandler offHandMatches(ItemLike... items) {
		return (event) -> {
			if (!event.getResult()) {
				event.setResult(event.getResult() || containsItem(event.getEntity().getOffhandItem().getItem(), items));
			}
		};
	}
	
	/**
	 * @param items Items to match
	 * @return A ComputePlayerAimStateEventHandler instance that sets the result to <code>true</code> when any hand matches any of the listed items
	 */
	static ComputePlayerAimStateEventHandler anyHandMatches(ItemLike... items) {
		return (event) -> {
			if (!event.getResult()) {
				event.setResult(containsItem(event.getEntity().getMainHandItem().getItem(), items) || containsItem(event.getEntity().getOffhandItem().getItem(), items));
			}
		};
	}
	
	private static boolean containsItem(Item itemToFind, ItemLike... items) {
		for (ItemLike item : items) {
			if (itemToFind.equals(item.asItem())) {
				return true;
			}
		}
		return false;
	}
}
