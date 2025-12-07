package com.github.exopandora.shouldersurfing.compat.plugin;

import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.compat.CobblemonCompat;
import org.jetbrains.annotations.NotNull;

public class CobblemonPlayerStateCallback implements IPlayerStateCallback
{
	@Override
	public @NotNull Result isRidingBoat(@NotNull IsRidingBoatContext context)
	{
		boolean useBoatControls = CobblemonCompat.hasActiveBoatBehaviour(context.vehicle()) ||
			CobblemonCompat.hasActiveSubmarineBehaviour(context.cameraEntity().getVehicle()) ||
			CobblemonCompat.hasActiveDolphinBehaviour(context.cameraEntity().getVehicle());
		return useBoatControls ? Result.TRUE : Result.PASS;
	}
}
