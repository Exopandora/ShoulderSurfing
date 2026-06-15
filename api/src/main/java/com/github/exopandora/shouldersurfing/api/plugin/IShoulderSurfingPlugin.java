package com.github.exopandora.shouldersurfing.api.plugin;

import com.github.exopandora.shouldersurfing.api.event.IEventBus;

public interface IShoulderSurfingPlugin {
	void register(IEventBus eventBus);
}
