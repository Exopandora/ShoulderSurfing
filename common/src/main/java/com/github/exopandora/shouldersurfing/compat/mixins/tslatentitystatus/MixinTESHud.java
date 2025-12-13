package com.github.exopandora.shouldersurfing.compat.mixins.tslatentitystatus;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "net.tslat.tes.core.hud.TESHud")
public class MixinTESHud
{
	@Redirect
	(
		method = "pickNewEntity",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/projectile/ProjectileUtil.getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
			remap = true
		),
		remap = false
	)
	private static EntityHitResult getEntityHitResult(Entity player, Vec3 startPos, Vec3 endPos, AABB boundingBox, Predicate<Entity> filter, double interactionRangeSq)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			double interactionRange = Math.sqrt(interactionRangeSq);
			PickContext pickContext = new PickContext.Builder(camera).build();
			return instance.getObjectPicker().pickEntities(pickContext, interactionRange, partialTick);
		}
		
		return ProjectileUtil.getEntityHitResult(player, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
	
	@Redirect
	(
		method = "pickNewEntity",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/level/Level.clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;",
			remap = true
		),
		remap = false
	)
	private static BlockHitResult clip(Level level, ClipContext clipContext)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			double interactionRange = camera.position().distanceTo(clipContext.getTo());
			PickContext pickContext = new PickContext.Builder(camera).build();
			return instance.getObjectPicker().pickBlocks(pickContext, interactionRange, partialTick);
		}
		
		return level.clip(clipContext);
	}
}
