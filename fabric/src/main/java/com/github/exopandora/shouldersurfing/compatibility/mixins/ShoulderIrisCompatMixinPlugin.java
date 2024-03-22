package com.github.exopandora.shouldersurfing.compatibility.mixins;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ShoulderIrisCompatMixinPlugin implements IMixinConfigPlugin
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
		try
		{
			SemanticVersion irisVersion = SemanticVersion.parse("1.6.17");
			return FabricLoader.getInstance().getAllMods().stream().anyMatch(modContainer ->
			{
				ModMetadata metadata = modContainer.getMetadata();
				return metadata.getId().equals("iris") && metadata.getVersion().compareTo(irisVersion) >= 0;
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
}
