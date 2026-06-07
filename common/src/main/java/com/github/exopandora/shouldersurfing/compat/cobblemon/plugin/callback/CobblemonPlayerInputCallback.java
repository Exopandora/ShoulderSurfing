package com.github.exopandora.shouldersurfing.compat.cobblemon.plugin.callback;

import com.github.exopandora.shouldersurfing.api.plugin.callback.IPlayerInputCallback;
import com.github.exopandora.shouldersurfing.compat.cobblemon.CobblemonCompat;

public class CobblemonPlayerInputCallback implements IPlayerInputCallback {
	@Override
	public boolean isForcingVanillaMovementInput(IsForcingVanillaMovementInputContext context) {
		return context.cameraEntity().isPassenger() && (
			CobblemonCompat.hasActiveBoatBehaviour(context.cameraEntity().getVehicle()) ||
				CobblemonCompat.hasActiveSubmarineBehaviour(context.cameraEntity().getVehicle()) ||
				CobblemonCompat.hasActiveDolphinBehaviour(context.cameraEntity().getVehicle())
		);
	}
}
