package com.github.exopandora.shouldersurfing.compat;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public abstract class ShoulderSurfingCompatMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {
		
	}
	
	@Override
	public String getRefMapperConfig() {
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	
	}
	
	protected static void addCommonCompatMixins(List<String> mixins) {
		addCGMMixins(mixins);
		addNeatMixins(mixins);
		addTheOneProbeMixins(mixins);
		addTslatEntityStatusMixins(mixins);
		addWildfireGenderMixins(mixins);
	}
	
	private static void addCGMMixins(List<String> mixins) {
		if (Mods.CGM.isLoaded()) {
			mixins.add("cgm.RecoilHandlerMixin");
		}
	}
	
	private static void addNeatMixins(List<String> mixins) {
		if (Mods.NEAT.isLoaded()) {
			mixins.add("neat.HealthBarRendererMixin");
		}
	}
	
	private static void addTheOneProbeMixins(List<String> mixins) {
		if (Mods.THE_ONE_PROBE.isLoaded()) {
			mixins.add("theoneprobe.OverlayRendererMixin");
		}
	}
	
	private static void addTslatEntityStatusMixins(List<String> mixins) {
		if (Mods.TSLAT_ENTITY_STATUS.isLoaded()) {
			mixins.add("tslatentitystatus.TESClientUtilMixin");
			mixins.add("tslatentitystatus.TESHudMixin");
		}
	}
	
	private static void addWildfireGenderMixins(List<String> mixins) {
		if (Mods.WILDFIRE_GENDER.isLoaded()) {
			mixins.add("wildfiregender.BreastRenderCommandMixin");
		}
	}
}
