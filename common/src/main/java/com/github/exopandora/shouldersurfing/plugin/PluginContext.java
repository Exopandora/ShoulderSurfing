package com.github.exopandora.shouldersurfing.plugin;

record PluginContext<T>(String modName, String modId, T source)
{
	public String formattedModName()
	{
		return this.modName + " (" + this.modId + ")";
	}
}
