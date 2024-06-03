package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler
{
	@Shadow
	private @Final Minecraft minecraft;
	
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
	
	@Inject
	(
		method = "handleMovePlayer",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/network/PacketThreadUtil.ensureRunningOnSameThread(Lnet/minecraft/network/IPacket;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/concurrent/ThreadTaskExecutor;)V",
			shift = Shift.AFTER
		)
	)
	private void handleMovePlayer(SPlayerPositionLookPacket packet, CallbackInfo ci)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			PlayerEntity player = this.minecraft.player;
			boolean isRelativeXRot = packet.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.X_ROT);
			boolean isRelativeYRot = packet.getRelativeArguments().contains(SPlayerPositionLookPacket.Flags.Y_ROT);
			
			if(isRelativeXRot && packet.getXRot() != 0.0F || !isRelativeXRot && player.xRot != packet.getXRot())
			{
				ShoulderSurfingCamera camera = instance.getCamera();
				camera.setXRot(isRelativeXRot ? camera.getXRot() + packet.getXRot() : packet.getXRot());
			}
			
			if(isRelativeYRot && packet.getYRot() != 0.0F || !isRelativeYRot && player.yRot != packet.getYRot())
			{
				ShoulderSurfingCamera camera = instance.getCamera();
				camera.setYRot(isRelativeYRot ? camera.getYRot() + packet.getYRot() : packet.getYRot());
			}
		}
	}
}
