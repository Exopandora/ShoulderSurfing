package com.github.exopandora.shouldersurfing.compat.mixin.cobblemon;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cobblemon.mod.common.util.PlayerExtensionsKt")
public class PlayerExtensionsKtMixin_1_6 {
	@Inject(
		method = "traceEntityCollision",
		at = @At("HEAD"),
		remap = false
	)
	private static <T extends Entity> void traceEntityCollision(
		Player player,
		float maxDistance,
		float stepDistance,
		Class<T> entityClass,
		T ignoreEntity,
		ClipContext.Fluid collideBlock,
		CallbackInfoReturnable<Object> ci
	) {
		Minecraft minecraft = Minecraft.getInstance();
		if (player == minecraft.player && player == minecraft.getCameraEntity()) {
			ShoulderSurfing instance = ShoulderSurfing.getInstance();
			if (instance.isShoulderSurfing() && !instance.getCrosshairRenderer().isCrosshairDynamic()) {
				instance.lookAtCrosshairTarget();
			}
		}
	}
}
