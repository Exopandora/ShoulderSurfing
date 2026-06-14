package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
	@ModifyVariable(
		method = "getFieldOfViewModifier",
		at = @At("HEAD"),
		index = 1,
		argsOnly = true
	)
	private boolean isFirstPerson(boolean isFirstPerson) {
		return isFirstPerson || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}
