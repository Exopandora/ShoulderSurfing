package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.IPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Mods
{
	CGM,
	CREATE,
	EMF,
	EPIC_FIGHT,
	SKIN_LAYERS,
	THE_ONE_PROBE,
	TSLAT_ENTITY_STATUS,
	WILDFIRE_GENDER;
	
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
