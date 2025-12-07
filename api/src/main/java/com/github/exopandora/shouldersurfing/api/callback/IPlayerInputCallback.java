package com.github.exopandora.shouldersurfing.api.callback;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

/**
 * This callback can be used to force vanilla movement inputs.
 * This can be useful when movement is computed server side via xxa, yya and zza fields.
 * The final result is calculated from all partial results using a logical OR.
 * @since 4.17.0
 */
public interface IPlayerInputCallback
{
	/**
	 * @param context The arguments of this callback.
	 * @return <code>true</code> when modifications should be disabled.
	 * @since 4.17.0
	 */
	boolean isForcingVanillaMovementInput(IsForcingVanillaMovementInputContext context);
	
	record IsForcingVanillaMovementInputContext(Minecraft minecraft, Entity cameraEntity)
	{
	}
}
