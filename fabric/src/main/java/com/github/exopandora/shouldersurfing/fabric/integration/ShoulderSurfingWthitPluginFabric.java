package com.github.exopandora.shouldersurfing.fabric.integration;

import com.github.exopandora.shouldersurfing.integration.ShoulderSurfingWthitPlugin;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;

public class ShoulderSurfingWthitPluginFabric extends ShoulderSurfingWthitPlugin
{
	@Override
	protected IObjectPicker defaultObjectPickerInstance()
	{
		return ObjectPicker.INSTANCE;
	}
}
