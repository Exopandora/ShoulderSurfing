package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.ShoulderSurfingCompatMixinPlugin;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;

import static com.github.exopandora.shouldersurfing.forge.Platform.parseVersionRangeSilent;

public class ShoulderSurfingCompatMixinPluginForge extends ShoulderSurfingCompatMixinPlugin
{
	@Override
	public List<String> getMixins()
	{
		List<String> mixins = new ArrayList<String>();
		addCobblemonMixins(mixins);
		addCommonCompatMixins(mixins);
		addCreateModMixins(mixins);
		addMtsMixins(mixins);
		return mixins.isEmpty() ? null : mixins;
	}
	
	private static void addMtsMixins(List<String> mixins)
	{
		if(Mods.MTS.isLoaded())
		{
			mixins.add("mts.MixinCameraSystem");
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
				mixins.add("cobblemon.MixinLocalPlayer");
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
}
