package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil
{
	@Inject
	(
		method = "getHitEntitiesAlong",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void getHitEntitiesAlong(Entity entity, AttackRange attackRange, Predicate<Entity> filter, ClipContext.Block blockMode, CallbackInfoReturnable<Either<BlockHitResult, Collection<EntityHitResult>>> cir)
	{
		if(!entity.level().isClientSide())
		{
			return;
		}
		
		Minecraft minecraft = Minecraft.getInstance();
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(entity == minecraft.player && instance.isShoulderSurfing())
		{
			Camera camera = minecraft.gameRenderer.getMainCamera();
			PickContext pickContext = new PickContext.Builder(camera)
				.withEntity(entity)
				.build();
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			ClipContext clipContext = pickContext.toClipContext(attackRange.effectiveMinRange(entity), partialTick);
			Vec3 from = clipContext.getFrom();
			Vec3 to = clipContext.getTo();
			Vec3 viewVector = from.vectorTo(to).normalize();
			double extraRange = entity.getKnownMovement().dot(viewVector);
			Vec3 rangeEnd = from.add(viewVector.scale(attackRange.effectiveMaxRange(entity) + Math.max(0.0, extraRange)));
			cir.setReturnValue(getHitEntitiesAlong(entity, from, to, filter, rangeEnd, attackRange.hitboxMargin(), blockMode));
		}
	}
	
	@Shadow
	private static Either<BlockHitResult, Collection<EntityHitResult>> getHitEntitiesAlong(Entity entity, Vec3 from, Vec3 to, Predicate<Entity> filter, Vec3 rangeEnd, float hitboxMargin, ClipContext.Block blockMode)
	{
		throw new AssertionError();
	}
}
