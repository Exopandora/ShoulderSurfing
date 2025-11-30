package com.github.exopandora.shouldersurfing;

import com.github.exopandora.shouldersurfing.compat.Mods;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

public interface IPlatform
{
	IPlatform INSTANCE = ServiceLoader.load(IPlatform.class).findFirst().orElseThrow();
	
	@Nullable
	String getModVersion(Mods mod);
	
	boolean isSameOrLaterVersion(String version, String baseVersion);
}
