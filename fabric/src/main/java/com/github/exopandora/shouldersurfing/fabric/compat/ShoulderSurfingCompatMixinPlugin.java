package com.github.exopandora.shouldersurfing.fabric.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShoulderSurfingCompatMixinPlugin implements IMixinConfigPlugin
{
	@Override
	public void onLoad(String mixinPackage)
	{
	
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
		
	}
	
	@Override
	public List<String> getMixins()
	{
		List<String> mixins = new ArrayList<String>();
		addCreateModMixins(mixins);
		addIrisMixins(mixins);
		return mixins.isEmpty() ? null : mixins;
	}
	
	private static void addIrisMixins(List<String> mixins)
	{
		String irisModVersion = Mods.IRIS.getModVersion();
		
		if(irisModVersion != null)
		{
			Version version = parseVersionSilent(irisModVersion);
			
			if(parseVersionPredicateSilent(">=1.7.0-snapshot").test(version))
			{
				mixins.add("iris.MixinSheets");
			}
			else if(parseVersionPredicateSilent("<1.7.0-snapshot >=1.6.17").test(version))
			{
				mixins.add("iris.MixinSheetsLegacy");
			}
		}
	}
	
	private static void addCreateModMixins(List<String> mixins)
	{
		String createModVersion = Mods.CREATE.getModVersion();
		
		if(createModVersion != null)
		{
			Version version = parseVersionSilent(createModVersion);
			
			if(parseVersionPredicateSilent(">=6.0.0").test(version))
			{
				mixins.add("create.MixinContraptionHandlerClient");
			}
			else if(parseVersionPredicateSilent("<6.0.0)").test(version))
			{
				mixins.add("create.MixinContraptionHandlerClientLegacy");
			}
		}
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
