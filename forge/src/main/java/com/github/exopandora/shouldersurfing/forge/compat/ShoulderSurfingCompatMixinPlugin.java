package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
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
		addOculusMixins(mixins);
		return mixins.isEmpty() ? null : mixins;
	}
	
	private static void addOculusMixins(List<String> mixins)
	{
		String oculusModVersion = Mods.OCULUS.getModVersion();
		
		if(oculusModVersion != null)
		{
			ArtifactVersion version = new DefaultArtifactVersion(oculusModVersion);
			
			if(parseVersionRangeSilent("[1.7.0-snapshot,)").containsVersion(version))
			{
				mixins.add("iris.MixinSheets_1_7_0");
			}
			else if(parseVersionRangeSilent("[1.6.15,1.7.0)").containsVersion(version))
			{
				mixins.add("iris.MixinSheets_1_6_15");
			}
		}
	}
	
	private static void addCreateModMixins(List<String> mixins)
	{
		String createModVersion = Mods.CREATE.getModVersion();
		
		if(createModVersion != null)
		{
			ArtifactVersion version = new DefaultArtifactVersion(createModVersion);
			
			if(parseVersionRangeSilent("[6.0.0,)").containsVersion(version))
			{
				mixins.add("create.MixinContraptionHandlerClient_6_0_0");
			}
			else if(parseVersionRangeSilent("(,6.0.0)").containsVersion(version))
			{
				mixins.add("create.MixinContraptionHandlerClient_0_5_0");
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
