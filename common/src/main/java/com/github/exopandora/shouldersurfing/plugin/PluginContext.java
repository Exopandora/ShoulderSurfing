package com.github.exopandora.shouldersurfing.plugin;

import java.nio.file.Path;

record PluginContext(String modName, String modId, Path path)
{
	public String formattedModName()
	{
		return this.modName + " (" + this.modId + ")";
	}
}
