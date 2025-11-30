package com.github.exopandora.shouldersurfing.compat;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public abstract class ShoulderSurfingCompatMixinPlugin implements IMixinConfigPlugin
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
	
	protected static void addCommonCompatMixins(List<String> mixins)
	{
		addCGMMixins(mixins);
		addCobblemonMixins(mixins);
		addEMFMixins(mixins);
		addEpicFightMixins(mixins);
		addTheOneProbeMixins(mixins);
		addTslatEntityStatusMixins(mixins);
		addWildfireGenderMixins(mixins);
	}
	
	private static void addCGMMixins(List<String> mixins)
	{
		if(Mods.CGM.isLoaded())
		{
			mixins.add("cgm.MixinRecoilHandler");
		}
	}
	
	private static void addCobblemonMixins(List<String> mixins)
	{
		if(Mods.COBBLEMON.isLoaded())
		{
			mixins.add("cobblemon.MixinPlayerExtensionsKt");
			mixins.add("cobblemon.MixinPokemonRenderer");
		}
	}
	
	private static void addEMFMixins(List<String> mixins)
	{
		if(Mods.EMF.isLoaded())
		{
			mixins.add("emf.MixinEMFModelPartCustom$EMFCube");
		}
	}
	
	private static boolean shouldApplyEpicFightMixins()
	{
		final String version = Mods.EPIC_FIGHT.getModVersion();
		if(version == null)
		{
			return false;
		}
		try
		{
			final String[] parts = version.split("\\.");
			final int major = Integer.parseInt(parts[0]);
			final int minor = Integer.parseInt(parts[1]);
			
			return major == 20 && minor < 14;
		}
		catch(Exception e)
		{
			ShoulderSurfingCommon.LOGGER.error("Failed to parse the Epic Fight mod version '{}': {}", version, e.toString());
			return false;
		}
	}
	
	private static void addEpicFightMixins(List<String> mixins)
	{
		if(Mods.EPIC_FIGHT.isLoaded())
		{
			if(shouldApplyEpicFightMixins())
			{
				mixins.add("epicfight.AccessorCamera");
				mixins.add("epicfight.MixinRenderEngine");
			}
			else
			{
				ShoulderSurfingCommon.LOGGER.info("Epic Fight lock-on support provided by Shoulder Surfing has been disabled due to breaking changes in '20.14.0'. For more details: https://github.com/Epic-Fight/epicfight/issues/2258");
			}
		}
	}
	
	private static void addTheOneProbeMixins(List<String> mixins)
	{
		if(Mods.THE_ONE_PROBE.isLoaded())
		{
			mixins.add("theoneprobe.MixinOverlayRenderer");
		}
	}
	
	private static void addTslatEntityStatusMixins(List<String> mixins)
	{
		if(Mods.TSLAT_ENTITY_STATUS.isLoaded())
		{
			mixins.add("tslatentitystatus.MixinTESHud");
		}
	}
	
	private static void addWildfireGenderMixins(List<String> mixins)
	{
		if(Mods.WILDFIRE_GENDER.isLoaded())
		{
			mixins.add("wildfiregender.MixinGenderLayer");
		}
	}
}
