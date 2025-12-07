package com.github.exopandora.shouldersurfing.compat.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IPlayerInputCallback;
import com.github.exopandora.shouldersurfing.compat.CobblemonCompat;

public class CobblemonPlayerInputCallback implements IPlayerInputCallback
{
	@Override
	public boolean isForcingVanillaMovementInput(IsForcingVanillaMovementInputContext context)
	{
		return context.cameraEntity().isPassenger() && (
			CobblemonCompat.hasActiveBoatBehaviour(context.cameraEntity().getVehicle()) ||
				CobblemonCompat.hasActiveSubmarineBehaviour(context.cameraEntity().getVehicle()) ||
				CobblemonCompat.hasActiveDolphinBehaviour(context.cameraEntity().getVehicle())
		);
	}
}
