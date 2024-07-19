package com.github.exopandora.shouldersurfing.fabric;

import com.github.exopandora.shouldersurfing.IPlatform;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.fabricmc.loader.api.FabricLoader;
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
		return FabricLoader.getInstance().getModContainer(modId)
			.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
			.orElse(null);
	}
}
