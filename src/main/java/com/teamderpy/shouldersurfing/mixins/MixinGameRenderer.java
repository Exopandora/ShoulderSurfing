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
	private ActiveRenderInfo activeRender;
	
	@Redirect
	(
		method = "getMouseOver",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/entity/projectile/ProjectileHelper.rayTraceEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"
		)
	)
	private EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distanceSq)
	{
		if(ShoulderSurfingHelper.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Pair<Vector3d, Vector3d> look = ShoulderSurfingHelper.calcShoulderSurfingLook(this.activeRender, shooter, Minecraft.getInstance().getRenderPartialTicks(), distanceSq);
			return ProjectileHelper.rayTraceEntities(shooter, look.getSecond(), look.getFirst(), boundingBox, filter, distanceSq);
		}
		
		return ProjectileHelper.rayTraceEntities(shooter, startVec, endVec, boundingBox, filter, distanceSq);
	}
}
