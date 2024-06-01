package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler
{
	@Inject
	(
		at = @At("TAIL"),
		method = "handleLogin"
	)
	private void handleLogin(CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().resetState();
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "handleRespawn"
	)
	private void handleRespawn(SRespawnPacket packet, CallbackInfo ci)
	{
		if(!packet.shouldKeepAllPlayerData())
		{
			ShoulderSurfingImpl.getInstance().resetState();
		}
	}
}
