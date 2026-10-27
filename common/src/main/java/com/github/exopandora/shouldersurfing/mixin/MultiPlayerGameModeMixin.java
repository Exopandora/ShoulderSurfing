package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
class MultiPlayerGameModeMixin {
	@Inject(
		method = "lambda$useItem$0",
		at = @At("TAIL"),
		cancellable = true
	)
	private void predict(
		InteractionHand hand,
		Player player,
		MutableObject<InteractionResult> mutableObject,
		int sequence,
		CallbackInfoReturnable<Packet<?>> cir
	) {
		var instance = ShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing() && instance.isPlayerRotationUpdateRequired()) {
			cir.setReturnValue(new ServerboundUseItemPacket(hand, sequence, player.getYRot(), player.getXRot()));
		}
	}
}
