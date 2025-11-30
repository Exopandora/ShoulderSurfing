package com.github.exopandora.shouldersurfing.compat;

public class CobblemonCompat
{
	private static final boolean SUPPORTS_RIDING = Mods.COBBLEMON.isSameOrLaterVersion("1.7.0");
	
	public static boolean supportsRiding()
	{
		return SUPPORTS_RIDING;
	}
}
