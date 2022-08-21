package com.teamderpy.shouldersurfing.compatibility;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;

public class ShoulderSurfingWailaPluginFabric extends ShoulderSurfingWailaPlugin
{
	@Override
	protected IObjectPicker defaultObjectPickerInstance()
	{
		return ObjectPicker.INSTANCE;
	}
}
