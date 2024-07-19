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
		if(Mods.CREATE == mod)
		{
			return findModVersionForId("create");
		}
		
		throw new IllegalArgumentException();
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
