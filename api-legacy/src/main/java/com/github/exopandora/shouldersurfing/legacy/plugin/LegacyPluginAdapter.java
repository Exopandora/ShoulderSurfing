package com.github.exopandora.shouldersurfing.legacy.plugin;

import com.github.exopandora.shouldersurfing.api.event.IEventBus;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.legacy.adapter.ShoulderSurfingRegistrarAdapter;
import com.github.exopandora.shouldersurfing.legacy.mixinduck.IShoulderSurfingLegacyPlugin;

public class LegacyPluginAdapter implements IShoulderSurfingPlugin {
	private final IShoulderSurfingLegacyPlugin legacyPlugin;
	
	public LegacyPluginAdapter(IShoulderSurfingLegacyPlugin legacyPlugin) {
		this.legacyPlugin = legacyPlugin;
	}
	
	@Override
	public void register(IEventBus eventBus) {
		this.legacyPlugin.register(new ShoulderSurfingRegistrarAdapter(eventBus));
	}
}
