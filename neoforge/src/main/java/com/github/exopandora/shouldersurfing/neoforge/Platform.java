package com.github.exopandora.shouldersurfing.neoforge;

import com.github.exopandora.shouldersurfing.IPlatform;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public class Platform implements IPlatform
{
	@Override
	public @Nullable String getModVersion(Mods mod)
	{
		return switch(mod)
		{
			case CREATE -> findModVersionForId("create");
		};
	}
	
	private static String findModVersionForId(String modId)
	{
		return FMLLoader.getLoadingModList().getMods().stream()
			.filter(info -> info.getModId().equals(modId))
			.findFirst()
			.map(info -> info.getVersion().toString())
			.orElse(null);
	}
}
