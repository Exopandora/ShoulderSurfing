package com.github.exopandora.shouldersurfing.compat.mixins.cobblemon;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after cobblemon mixin */)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
	public MixinLocalPlayer(ClientLevel level, GameProfile gameProfile)
	{
		super(level, gameProfile);
	}
	
	@Inject
	(
		method = "pick",
		at = @At("HEAD"),
		cancellable = true
	)
	private void pick(double interactionRange, float partialTick, boolean stopOnFluid, CallbackInfoReturnable<HitResult> cir)
	{
		if(ShoulderSurfing.getInstance().isShoulderSurfing())
		{
			cir.setReturnValue(super.pick(interactionRange, partialTick, stopOnFluid));
		}
	}
}
