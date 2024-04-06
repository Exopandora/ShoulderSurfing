package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.AbstractClientPlayerEntityDuck;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.math.vector.Vector3d;
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
			target = "net/minecraft/client/entity/player/AbstractClientPlayerEntity.getDeltaMovement()Lnet/minecraft/util/math/vector/Vector3d;"
		),
		require = 0
	)
	protected Vector3d setupRotations(AbstractClientPlayerEntity entity, AbstractClientPlayerEntity player, MatrixStack poseStack, float bob, float yBodyRot, float partialTick)
	{
		return ((AbstractClientPlayerEntityDuck) player).shouldersurfing$getDeltaMovementLerped(partialTick);
	}
}
