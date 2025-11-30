package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.IPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Mods
{
	CGM,
	COBBLEMON,
	CREATE,
	CURIOS,
	EMF,
	EPIC_FIGHT,
	IRIS,
	MTS,
	OCULUS,
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
	
	public boolean isSameOrLaterVersion(String version)
	{
		String modVersion = this.getModVersion();
		
		if(modVersion == null)
		{
			return false;
		}
		
		return IPlatform.INSTANCE.isSameOrLaterVersion(modVersion, version);
	}
	
	public boolean isLoaded()
	{
		return this.getModVersion() != null;
	}
}
