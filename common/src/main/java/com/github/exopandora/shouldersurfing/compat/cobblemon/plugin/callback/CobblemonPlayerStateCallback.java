package com.github.exopandora.shouldersurfing.compat.cobblemon.plugin.callback;

import com.github.exopandora.shouldersurfing.api.plugin.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.compat.cobblemon.CobblemonCompat;
import org.jetbrains.annotations.NotNull;

public class CobblemonPlayerStateCallback implements IPlayerStateCallback {
	@Override
	public @NotNull Result isRidingBoat(@NotNull IsRidingBoatContext context) {
		boolean useBoatControls = CobblemonCompat.hasActiveBoatBehaviour(context.vehicle()) ||
			CobblemonCompat.hasActiveSubmarineBehaviour(context.cameraEntity().getVehicle()) ||
			CobblemonCompat.hasActiveDolphinBehaviour(context.cameraEntity().getVehicle());
		return useBoatControls ? Result.TRUE : Result.PASS;
	}
}
