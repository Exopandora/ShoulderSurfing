package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.IPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Mods
{
	CGM,
	COBBLEMON,
	CREATE
	{
		@Override
		public boolean isLoaded()
		{
			return super.isLoaded() && existsClass("com.simibubi.create.Create");
		}
	},
	CREATE_FLY
	{
		@Override
		public boolean isLoaded()
		{
			return super.isLoaded() && existsClass("com.zurrtum.create.Create");
		}
	},
	CURIOS,
	MTS,
	THE_ONE_PROBE,
	TSLAT_ENTITY_STATUS,
	;
	
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
	
	private static boolean existsClass(String className)
	{
		try
		{
			Class.forName(className);
		}
		catch(Throwable t)
		{
			return false;
		}
		
		return true;
	}
}
