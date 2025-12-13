package com.github.exopandora.shouldersurfing.compat;

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
//		return hasActiveBehaviour(vehicle, BoatBehaviour.Companion.getKEY());
		return false;
	}
	
	public static boolean hasActiveSubmarineBehaviour(@Nullable Entity vehicle)
	{
//		return hasActiveBehaviour(vehicle, SubmarineBehaviour.Companion.getKEY());
		return false;
	}
	
	public static boolean hasActiveDolphinBehaviour(@Nullable Entity vehicle)
	{
//		return hasActiveBehaviour(vehicle, DolphinBehaviour.Companion.getKEY());
		return false;
	}
	
//	private static boolean hasActiveBehaviour(@Nullable Entity entity, Identifier behaviour)
//	{
//		if(entity instanceof PokemonEntity pokemon && pokemon.getRidingController() != null)
//		{
//			ActiveRidingContext ridingContext = pokemon.getRidingController().getContext();
//			return ridingContext != null && ridingContext.getSettings().getKey().equals(behaviour);
//		}
//
//		return false;
//	}
}
