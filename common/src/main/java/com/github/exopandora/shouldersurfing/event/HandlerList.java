package com.github.exopandora.shouldersurfing.event;

import com.github.exopandora.shouldersurfing.api.event.Event;
import com.github.exopandora.shouldersurfing.plugin.PluginContainer;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Consumer;

class HandlerList {
	private final PriorityQueue<EventHandler> handlers = new PriorityQueue<EventHandler>(
		Comparator.comparing(EventHandler::priority).thenComparing(EventHandler::index)
	);
	private final boolean cancellable;
	
	public HandlerList(boolean cancellable) {
		this.cancellable = cancellable;
	}
	
	public void add(int priority, Consumer<Event> consumer, PluginContainer container) {
		this.handlers.add(new EventHandler(priority, this.handlers.size(), container, consumer));
	}
	
	public boolean isEventCancellable() {
		return this.cancellable;
	}
	
	public Collection<EventHandler> getListeners() {
		return this.handlers;
	}
}
