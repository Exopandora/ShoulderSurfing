package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after essential client, so turn method gets overwritten */)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
	public MixinLocalPlayer(ClientLevel level, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey)
	{
		super(level, gameProfile, profilePublicKey);
	}
	
	@Override
	public void turn(double yRot, double xRot)
	{
		if(!ShoulderSurfingImpl.getInstance().getCamera().turn((LocalPlayer) (Object) this, yRot, xRot))
		{
			super.turn(yRot, xRot);
		}
	}
}
