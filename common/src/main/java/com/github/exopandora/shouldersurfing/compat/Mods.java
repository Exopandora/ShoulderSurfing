package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.IPlatform;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum Mods
{
	CREATE,
	IRIS,
	OCULUS;
	
	private final Function<Mods, @Nullable String> version = Util.memoize(IPlatform.INSTANCE::getModVersion);
	
	@Nullable
	public String getModVersion()
	{
		return this.version.apply(this);
	}
	
	public boolean isLoaded()
	{
		return this.getModVersion() != null;
	}
}
