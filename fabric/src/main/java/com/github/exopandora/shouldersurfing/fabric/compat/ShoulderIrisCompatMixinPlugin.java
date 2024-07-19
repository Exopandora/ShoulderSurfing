package com.github.exopandora.shouldersurfing.fabric.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ShoulderIrisCompatMixinPlugin implements IMixinConfigPlugin
{
	private final Map<String, Supplier<Predicate<Version>>> rules = new HashMap<String, Supplier<Predicate<Version>>>();
	
	@Override
	public void onLoad(String mixinPackage)
	{
		this.rules.put(mixinPackage + ".MixinSheets", () -> parseVersionPredicateSilent(">=1.7.0-snapshot"));
		this.rules.put(mixinPackage + ".MixinSheetsLegacy", () -> parseVersionPredicateSilent("<1.7.0-snapshot >=1.6.17"));
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if(this.rules.containsKey(mixinClassName))
		{
			String irisVersion = Mods.IRIS.getModVersion();
			
			if(irisVersion != null)
			{
				Predicate<Version> versionPredicate = this.rules.get(mixinClassName).get();
				return versionPredicate.test(parseVersionSilent(irisVersion));
			}
		}
		
		return false;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
		
	}
	
	@Override
	public List<String> getMixins()
	{
		return null;
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		
	}
	
	private static Version parseVersionSilent(String version)
	{
		try
		{
			return Version.parse(version);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static VersionPredicate parseVersionPredicateSilent(String predicate)
	{
		try
		{
			return VersionPredicate.parse(predicate);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
