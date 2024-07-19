package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ShoulderOculusCompatMixinPlugin implements IMixinConfigPlugin
{
	private final Map<String, Supplier<Predicate<ArtifactVersion>>> rules = new HashMap<String, Supplier<Predicate<ArtifactVersion>>>();
	
	@Override
	public void onLoad(String mixinPackage)
	{
		this.rules.put(mixinPackage + ".MixinSheets", () -> parseVersionRangeSilent("[1.7.0-snapshot,)")::containsVersion);
		this.rules.put(mixinPackage + ".MixinSheetsLegacy", () -> parseVersionRangeSilent("[1.6.15,1.7.0)")::containsVersion);
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
			String oculusVersion = Mods.OCULUS.getModVersion();
			
			if(oculusVersion != null)
			{
				Predicate<ArtifactVersion> versionPredicate = this.rules.get(mixinClassName).get();
				return versionPredicate.test(new DefaultArtifactVersion(oculusVersion));
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
	
	private static VersionRange parseVersionRangeSilent(String predicate)
	{
		try
		{
			return VersionRange.createFromVersionSpec(predicate);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
