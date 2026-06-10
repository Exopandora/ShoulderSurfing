package com.github.exopandora.shouldersurfing.event;

import com.github.exopandora.shouldersurfing.api.event.Event;
import com.github.exopandora.shouldersurfing.plugin.PluginContainer;

import java.util.function.Consumer;

record EventHandler(
	int priority,
	int index,
	PluginContainer plugin,
	Consumer<Event> consumer
) {
	void handle(Event event) {
		this.consumer.accept(event);
	}
}
