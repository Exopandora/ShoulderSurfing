package com.github.exopandora.shouldersurfing.forge;

import com.github.exopandora.shouldersurfing.IPlatform;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public class Platform implements IPlatform
{
	@Override
	public @Nullable String getModVersion(Mods mod)
	{
		return switch(mod)
		{
			case CURIOS, IRIS, WILDFIRE_GENDER -> null;
			case CGM -> findModVersionForId("cgm");
			case COBBLEMON -> findModVersionForId("cobblemon");
			case CREATE -> findModVersionForId("create");
			case EMF -> findModVersionForId("entity_model_features");
			case EPIC_FIGHT -> findModVersionForId("epicfight");
			case OCULUS -> findModVersionForId("oculus");
			case SKIN_LAYERS -> findModVersionForId("skinlayers3d");
			case THE_ONE_PROBE -> findModVersionForId("theoneprobe");
			case TSLAT_ENTITY_STATUS -> findModVersionForId("tslatentitystatus");
		};
	}
	
	@SuppressWarnings("UnstableApiUsage")
	private static String findModVersionForId(String modId)
	{
		return FMLLoader.getLoadingModList().getMods().stream()
			.filter(info -> info.getModId().equals(modId))
			.findFirst()
			.map(info -> info.getVersion().toString())
			.orElse(null);
	}
}
