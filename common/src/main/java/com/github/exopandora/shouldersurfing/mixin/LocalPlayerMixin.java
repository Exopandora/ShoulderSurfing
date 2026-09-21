package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after essential client, so turn method gets overwritten */)
abstract class LocalPlayerMixin extends AbstractClientPlayer {
	private LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
		super(level, gameProfile);
	}
	
	@Override
	public void turn(double yRot, double xRot) {
		var instance = ShoulderSurfing.getInstance();
		var camera = instance.getCamera();
		if (!camera.turn((LocalPlayer) (Object) this, yRot, xRot)) {
			super.turn(yRot, xRot);
			if (instance.isTemporaryFirstPerson()) {
				camera.setXRot(this.getXRot());
				camera.setYRot(this.getYRot());
			}
		}
	}
}
