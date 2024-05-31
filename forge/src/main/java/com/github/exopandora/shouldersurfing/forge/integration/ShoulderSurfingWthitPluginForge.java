package com.github.exopandora.shouldersurfing.forge.integration;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.integration.ShoulderSurfingWthitPlugin;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;

@WailaPlugin(id = ShoulderSurfingCommon.MOD_ID + ":waila_plugin")
public class ShoulderSurfingWthitPluginForge extends ShoulderSurfingWthitPlugin
{
	@Override
	protected IObjectPicker defaultObjectPickerInstance()
	{
		return ObjectPicker.INSTANCE;
	}
}
