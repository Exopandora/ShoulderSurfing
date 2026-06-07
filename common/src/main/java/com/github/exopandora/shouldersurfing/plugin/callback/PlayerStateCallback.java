package com.github.exopandora.shouldersurfing.plugin.callback;

import com.github.exopandora.shouldersurfing.api.client.CrosshairType;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;
import com.github.exopandora.shouldersurfing.api.plugin.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import org.jetbrains.annotations.NotNull;

public class PlayerStateCallback implements IPlayerStateCallback {
	@Override
	public @NotNull Result isInteracting(@NotNull IsInteractingContext context) {
		if (context.cameraEntity().isFallFlying()) {
			return Result.FALSE;
		} else if (Config.CLIENT.getObjectPickerConfig().getPickVector() == PickVector.PLAYER && Config.CLIENT.getCrosshairConfig().getCrosshairType() == CrosshairType.DYNAMIC) {
			return Result.FALSE;
		}
		return IPlayerStateCallback.super.isInteracting(context);
	}
	
	@Override
	public @NotNull Result isAttacking(@NotNull IsAttackingContext context) {
		if (context.cameraEntity().isFallFlying()) {
			return Result.FALSE;
		}
		return IPlayerStateCallback.super.isAttacking(context);
	}
	
	@Override
	public @NotNull Result isPicking(@NotNull IsPickingContext context) {
		if (context.cameraEntity().isFallFlying()) {
			return Result.FALSE;
		}
		return IPlayerStateCallback.super.isPicking(context);
	}
}
