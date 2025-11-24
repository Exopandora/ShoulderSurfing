package com.github.exopandora.shouldersurfing.neoforge.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.ShoulderSurfingCompatMixinPlugin;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoulderSurfingCompatMixinPluginNeoForge extends ShoulderSurfingCompatMixinPlugin
{
	@Override
	public List<String> getMixins()
	{
		List<String> mixins = new ArrayList<String>();
		addCobblemonMixins(mixins);
		addCommonCompatMixins(mixins);
		addCreateModMixins(mixins);
		addShaderMixins(mixins);
		return mixins.isEmpty() ? null : mixins;
	}
	
	private static void addShaderMixins(List<String> mixins)
	{
		ArtifactVersion version = highestShaderVersion();
		
		if(version != null)
		{
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
	
	private static void addCobblemonMixins(List<String> mixins)
	{
		String cobblemonVersion = Mods.COBBLEMON.getModVersion();
		
		if(cobblemonVersion != null)
		{
			ArtifactVersion version = new DefaultArtifactVersion(cobblemonVersion);
			
			if(parseVersionRangeSilent("[1.7.0,)").containsVersion(version))
			{
				mixins.add("cobblemon.MixinPlayerExtensionsKt_1_7");
			}
			else if(parseVersionRangeSilent("(,1.7.0)").containsVersion(version))
			{
				mixins.add("cobblemon.MixinPlayerExtensionsKt_1_6");
			}
			
			mixins.add("cobblemon.MixinPokemonRenderer");
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
	
	private static @Nullable ArtifactVersion highestShaderVersion()
	{
		List<String> shaderVersions = new ArrayList<String>();
		shaderVersions.add(Mods.OCULUS.getModVersion());
		shaderVersions.add(Mods.IRIS.getModVersion());
		shaderVersions.removeIf(Objects::isNull);
		return switch(shaderVersions.size())
		{
			case 0 -> null;
			case 1 -> new DefaultArtifactVersion(shaderVersions.getFirst());
			default -> shaderVersions.stream()
				.map(DefaultArtifactVersion::new)
				.sorted()
				.findFirst()
				.orElse(null);
		};
	}
}
