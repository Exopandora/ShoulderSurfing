package com.teamderpy.shouldersurfing.compatibility;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;

@WailaPlugin(id = ShoulderSurfing.MODID + ":waila_plugin")
public class ShoulderSurfingWailaPluginForge extends ShoulderSurfingWailaPlugin
{
	@Override
	protected IObjectPicker defaultObjectPickerInstance()
	{
		return ObjectPicker.INSTANCE;
	}
}
