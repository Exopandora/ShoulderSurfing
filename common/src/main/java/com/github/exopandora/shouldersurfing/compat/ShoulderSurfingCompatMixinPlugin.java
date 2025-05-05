package com.github.exopandora.shouldersurfing.compat;

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
		addCreateModMixins(mixins);
		addEMFMixins(mixins);
		addEpicFightMixins(mixins);
		addTheOneProbeMixins(mixins);
		addTslatEntityStatusMixins(mixins);
	}
	
	private static void addCGMMixins(List<String> mixins)
	{
		if(Mods.CGM.isLoaded())
		{
			mixins.add("cgm.MixinRecoilHandler");
		}
	}
	
	private static void addCreateModMixins(List<String> mixins)
	{
		if(Mods.CREATE.isLoaded())
		{
			mixins.add("create.MixinContraptionHandlerClient");
		}
	}
	
	private static void addEMFMixins(List<String> mixins)
	{
		if(Mods.EMF.isLoaded())
		{
			mixins.add("emf.MixinEMFModelPartCustom$EMFCube");
		}
	}
	
	private static void addEpicFightMixins(List<String> mixins)
	{
		if(Mods.EPIC_FIGHT.isLoaded())
		{
			mixins.add("epicfight.AccessorCamera");
			mixins.add("epicfight.MixinRenderEngine");
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
}
