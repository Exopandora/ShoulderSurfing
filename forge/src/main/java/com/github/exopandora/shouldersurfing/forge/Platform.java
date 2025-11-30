package com.github.exopandora.shouldersurfing.forge;

import com.github.exopandora.shouldersurfing.IPlatform;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.Nullable;

public class Platform implements IPlatform
{
	@Override
	public @Nullable String getModVersion(Mods mod)
	{
		return switch(mod)
		{
			case CREATE_FLY, CURIOS -> null;
			case CGM -> findModVersionForId("cgm");
			case COBBLEMON -> findModVersionForId("cobblemon");
			case CREATE -> findModVersionForId("create");
			case MTS -> findModVersionForId("mts");
			case THE_ONE_PROBE -> findModVersionForId("theoneprobe");
			case TSLAT_ENTITY_STATUS -> findModVersionForId("tslatentitystatus");
		};
	}
	
	@Override
	public boolean isSameOrLaterVersion(String version, String baseVersion)
	{
		return parseVersionRangeSilent("[" + baseVersion + ",)").containsVersion(new DefaultArtifactVersion(version));
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
	
	public static VersionRange parseVersionRangeSilent(String predicate)
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
