package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.ShoulderSurfingCompatMixinPlugin;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;

public class ShoulderSurfingCompatMixinPluginForge extends ShoulderSurfingCompatMixinPlugin
{
	@Override
	public List<String> getMixins()
	{
		List<String> mixins = new ArrayList<String>();
		addCommonCompatMixins(mixins);
		addCreateModMixins(mixins);
		addOculusMixins(mixins);
		addSkinLayersMixins(mixins);
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
	
	private static void addSkinLayersMixins(List<String> mixins)
	{
		String skinLayersModVersion = Mods.SKIN_LAYERS.getModVersion();
		
		if(skinLayersModVersion != null)
		{
			ArtifactVersion version = new DefaultArtifactVersion(skinLayersModVersion);
			
			if(parseVersionRangeSilent("[1_6_6,)").containsVersion(version))
			{
				mixins.add("skinlayers.MixinCustomizableModelPart_1_6_6");
			}
			else if(parseVersionRangeSilent("(,1.6.6)").containsVersion(version))
			{
				mixins.add("skinlayers.MixinCustomizableModelPart_1_6_5");
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
