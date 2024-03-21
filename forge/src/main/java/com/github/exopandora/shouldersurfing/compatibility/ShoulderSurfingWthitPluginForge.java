package com.github.exopandora.shouldersurfing.compatibility;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;

@WailaPlugin(id = ShoulderSurfing.MODID + ":waila_plugin")
public class ShoulderSurfingWthitPluginForge extends ShoulderSurfingWthitPlugin
{
	@Override
	protected IObjectPicker defaultObjectPickerInstance()
	{
		return ObjectPicker.INSTANCE;
	}
}
