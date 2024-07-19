package com.github.exopandora.shouldersurfing;

import com.github.exopandora.shouldersurfing.compat.Mods;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public interface IPlatform
{
	IPlatform INSTANCE = ServiceLoader.load(IPlatform.class).iterator().next();
	
	@Nullable
	String getModVersion(Mods mod);
}
