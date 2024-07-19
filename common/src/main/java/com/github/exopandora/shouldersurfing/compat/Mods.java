package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.IPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Mods
{
	CREATE,
	IRIS,
	OCULUS;
	
	private static final Map<Mods, @Nullable String> MODS_TO_VERSION = new HashMap<Mods, String>();
	
	@Nullable
	public String getModVersion()
	{
		return MODS_TO_VERSION.computeIfAbsent(this, IPlatform.INSTANCE::getModVersion);
	}
	
	public boolean isLoaded()
	{
		return this.getModVersion() != null;
	}
}
