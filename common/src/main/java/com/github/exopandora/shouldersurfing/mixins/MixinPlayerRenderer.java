package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.AbstractClientPlayerDuck;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer
{
	@Redirect
	(
		method = "setupRotations",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/player/AbstractClientPlayer.getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"
		),
		require = 0
	)
	protected Vec3 setupRotations(AbstractClientPlayer entity, AbstractClientPlayer player, PoseStack poseStack, float bob, float yBodyRot, float partialTick)
	{
		return ((AbstractClientPlayerDuck) player).shouldersurfing$getDeltaMovementLerped(partialTick);
	}
}
