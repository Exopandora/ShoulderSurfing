package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.IPlatform;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.Nullable;

public class Platform implements IPlatform
{
	@Override
	public @Nullable String getModVersion(Mods mod)
	{
		return switch(mod)
		{
			case CGM, CURIOS, EPIC_FIGHT, MTS -> null;
			case CREATE -> findModVersionForId("create");
			case EMF -> findModVersionForId("entity_model_features");
			case SKIN_LAYERS -> findModVersionForId("skinlayers3d");
			case THE_ONE_PROBE -> findModVersionForId("theoneprobe");
			case TSLAT_ENTITY_STATUS -> findModVersionForId("tslatentitystatus");
			case WILDFIRE_GENDER -> findModVersionForId("wildfire_gender");
		};
	}
	
	private static String findModVersionForId(String modId)
	{
		return FabricLoader.getInstance().getModContainer(modId)
			.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
			.orElse(null);
	}
	
	public static Version parseVersionSilent(String version)
	{
		try
		{
			return Version.parse(version);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static VersionPredicate parseVersionPredicateSilent(String predicate)
	{
		try
		{
			return VersionPredicate.parse(predicate);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
