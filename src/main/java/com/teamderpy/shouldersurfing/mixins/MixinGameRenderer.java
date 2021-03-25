package com.teamderpy.shouldersurfing.mixins;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
	@Shadow
	private ActiveRenderInfo mainCamera;
	
	@Redirect
	(
		method = "pick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/entity/projectile/ProjectileHelper.getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"
		)
	)
	private EntityRayTraceResult getEntityHitResult(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distanceSq)
	{
		if(ShoulderSurfingHelper.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Pair<Vector3d, Vector3d> look = ShoulderSurfingHelper.calcShoulderSurfingLook(this.mainCamera, shooter, Minecraft.getInstance().getFrameTime(), distanceSq);
			return ProjectileHelper.getEntityHitResult(shooter, look.getSecond(), look.getFirst(), boundingBox, filter, distanceSq);
		}
		
		return ProjectileHelper.getEntityHitResult(shooter, startVec, endVec, boundingBox, filter, distanceSq);
	}
}
