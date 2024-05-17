package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after essential client, so turn method gets overwritten */)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
	public MixinLocalPlayer(ClientLevel level, GameProfile gameProfile)
	{
		super(level, gameProfile);
	}
	
	@Override
	public void turn(double yRot, double xRot)
	{
		if(!ShoulderRenderer.getInstance().turn(this, yRot, xRot))
		{
			super.turn(yRot, xRot);
		}
	}
}
