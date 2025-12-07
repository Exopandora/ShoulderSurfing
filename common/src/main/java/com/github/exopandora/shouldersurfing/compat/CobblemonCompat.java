package com.github.exopandora.shouldersurfing.compat;

import com.cobblemon.mod.common.api.riding.behaviour.ActiveRidingContext;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.BoatBehaviour;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.DolphinBehaviour;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.SubmarineBehaviour;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class CobblemonCompat
{
	private static final boolean SUPPORTS_RIDING = Mods.COBBLEMON.isSameOrLaterVersion("1.7.0");
	
	public static boolean supportsRiding()
	{
		return SUPPORTS_RIDING;
	}
	
	public static boolean hasActiveBoatBehaviour(@Nullable Entity vehicle)
	{
		return hasActiveBehaviour(vehicle, BoatBehaviour.Companion.getKEY());
	}
	
	public static boolean hasActiveSubmarineBehaviour(@Nullable Entity vehicle)
	{
		return hasActiveBehaviour(vehicle, SubmarineBehaviour.Companion.getKEY());
	}
	
	public static boolean hasActiveDolphinBehaviour(@Nullable Entity vehicle)
	{
		return hasActiveBehaviour(vehicle, DolphinBehaviour.Companion.getKEY());
	}
	
	private static boolean hasActiveBehaviour(@Nullable Entity entity, ResourceLocation behaviour)
	{
		if(entity instanceof PokemonEntity pokemon && pokemon.getRidingController() != null)
		{
			ActiveRidingContext ridingContext = pokemon.getRidingController().getContext();
			return ridingContext != null && ridingContext.getSettings().getKey().equals(behaviour);
		}
		
		return false;
	}
}
