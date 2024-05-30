package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@Redirect
	(
		method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/projectile/ProjectileUtil.getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
		)
	)
	private EntityHitResult getEntityHitResult(Entity shooter, Vec3 startPos, Vec3 endPos, AABB boundingBox, Predicate<Entity> filter, double interactionRangeSq)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			PickContext pickContext = new PickContext.Builder(this.getMainCamera())
				.withEntity(shooter)
				.build();
			double interactionRange = Math.sqrt(interactionRangeSq);
			float partialTick = Minecraft.getInstance().getFrameTime();
			return instance.getObjectPicker().pickEntities(pickContext, interactionRange, partialTick);
		}
		
		return ProjectileUtil.getEntityHitResult(shooter, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
}
