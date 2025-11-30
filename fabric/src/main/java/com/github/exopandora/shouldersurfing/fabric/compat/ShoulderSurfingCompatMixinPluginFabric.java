package com.github.exopandora.shouldersurfing.fabric.compat;

import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.ShoulderSurfingCompatMixinPlugin;
import net.fabricmc.loader.api.Version;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;

import static com.github.exopandora.shouldersurfing.fabric.Platform.parseVersionPredicateSilent;
import static com.github.exopandora.shouldersurfing.fabric.Platform.parseVersionSilent;

public class ShoulderSurfingCompatMixinPluginFabric extends ShoulderSurfingCompatMixinPlugin
{
	@Override
	public List<String> getMixins()
	{
		List<String> mixins = new ArrayList<String>();
		addCobblemonMixins(mixins);
		addCommonCompatMixins(mixins);
		addCreateModMixins(mixins);
		addCreateFlyMixins(mixins);
		return mixins.isEmpty() ? null : mixins;
	}
	
	private static void addCreateModMixins(List<String> mixins)
	{
		if(Mods.CREATE.isLoaded())
		{
			Version version = parseVersionSilent(Mods.CREATE.getModVersion());
			
			if(parseVersionPredicateSilent(">=6.0.0").test(version))
			{
				mixins.add("create.MixinContraptionHandlerClient_6_0_0");
			}
			else if(parseVersionPredicateSilent("<6.0.0").test(version))
			{
				mixins.add("create.MixinContraptionHandlerClient_0_5_0");
			}
		}
	}
	
	private static void addCreateFlyMixins(List<String> mixins)
	{
		if(Mods.CREATE_FLY.isLoaded())
		{
			mixins.add("createfly.MixinContraptionHandlerClient");
		}
	}
	
	private static void addCobblemonMixins(List<String> mixins)
	{
		String cobblemonVersion = Mods.COBBLEMON.getModVersion();
		
		if(cobblemonVersion != null)
		{
			Version version = parseVersionSilent(cobblemonVersion);
			
			if(parseVersionPredicateSilent(">=1.7.0").test(version))
			{
				mixins.add("cobblemon.MixinPlayerExtensionsKt_1_7");
			}
			else if(parseVersionPredicateSilent("<1.7.0").test(version))
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
