package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener extends ClientCommonPacketListenerImpl
{
	protected MixinClientPacketListener(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie)
	{
		super(minecraft, connection, commonListenerCookie);
	}
	
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
	private void handleRespawn(ClientboundRespawnPacket packet, CallbackInfo ci)
	{
		if(!packet.shouldKeep(ClientboundRespawnPacket.KEEP_ALL_DATA))
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
			target = "net/minecraft/network/protocol/PacketUtils.ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
			shift = Shift.AFTER
		)
	)
	private void handleMovePlayer(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			Player player = this.minecraft.player;
			boolean isRelativeXRot = packet.relatives().contains(Relative.X_ROT);
			boolean isRelativeYRot = packet.relatives().contains(Relative.Y_ROT);
			
			if(isRelativeXRot && packet.change().xRot() != 0.0F || !isRelativeXRot && player.getXRot() != packet.change().xRot())
			{
				ShoulderSurfingCamera camera = instance.getCamera();
				camera.setXRot(isRelativeXRot ? camera.getXRot() + packet.change().xRot() : packet.change().xRot());
			}
			
			if(isRelativeYRot && packet.change().yRot() != 0.0F || !isRelativeYRot && player.getYRot() != packet.change().yRot())
			{
				ShoulderSurfingCamera camera = instance.getCamera();
				camera.setYRot(isRelativeYRot ? camera.getYRot() + packet.change().yRot() : packet.change().yRot());
			}
		}
	}
}
