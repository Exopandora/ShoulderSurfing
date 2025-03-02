package com.github.exopandora.shouldersurfing.compat.mixins.tslatentitystatus;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
			target = "net/minecraft/entity/projectile/ProjectileHelper.getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;",
			remap = true
		),
		remap = false
	)
	private static EntityRayTraceResult getEntityHitResult(Entity player, Vector3d startPos, Vector3d endPos, AxisAlignedBB boundingBox, Predicate<Entity> filter, double interactionRangeSq)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaFrameTime();
			double interactionRange = Math.sqrt(interactionRangeSq);
			PickContext pickContext = new PickContext.Builder(camera).build();
			return instance.getObjectPicker().pickEntities(pickContext, interactionRange, partialTick);
		}
		
		return ProjectileHelper.getEntityHitResult(player, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
	
	@Redirect
	(
		method = "pickNewEntity",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/World.clip(Lnet/minecraft/util/math/RayTraceContext;)Lnet/minecraft/util/math/BlockRayTraceResult;",
			remap = true
		),
		remap = false
	)
	private static BlockRayTraceResult clip(World level, RayTraceContext clipContext)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaFrameTime();
			double interactionRange = camera.getPosition().distanceTo(clipContext.getTo());
			PickContext pickContext = new PickContext.Builder(camera).build();
			return instance.getObjectPicker().pickBlocks(pickContext, interactionRange, partialTick);
		}
		
		return level.clip(clipContext);
	}
}
