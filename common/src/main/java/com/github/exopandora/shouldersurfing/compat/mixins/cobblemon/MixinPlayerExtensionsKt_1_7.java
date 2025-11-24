package com.github.exopandora.shouldersurfing.compat.mixins.cobblemon;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cobblemon.mod.common.util.PlayerExtensionsKt")
public class MixinPlayerExtensionsKt_1_7
{
	@Inject
	(
		method = "traceFirstEntityCollision",
		at = @At("HEAD"),
		remap = false
	)
	private static <T extends Entity> void traceFirstEntityCollision(LivingEntity entity, float maxDistance, float stepDistance, Class<T> entityClass, T ignoreEntity, ClipContext.Fluid collideBlock, CallbackInfoReturnable<Object> ci)
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(entity == minecraft.player && entity == minecraft.getCameraEntity())
		{
			ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
			
			if(instance.isShoulderSurfing() && !instance.getCrosshairRenderer().isCrosshairDynamic(entity))
			{
				instance.lookAtCrosshairTarget();
			}
		}
	}
}
