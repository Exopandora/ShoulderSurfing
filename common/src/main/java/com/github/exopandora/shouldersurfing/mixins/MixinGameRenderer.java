package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

import static org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@Redirect
	(
		method = "pick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/entity/projectile/ProjectileHelper.getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"
		)
	)
	private EntityRayTraceResult getEntityHitResult(Entity shooter, Vector3d startPos, Vector3d endPos, AxisAlignedBB boundingBox, Predicate<Entity> filter, double interactionRangeSq)
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
		
		return ProjectileHelper.getEntityHitResult(shooter, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
	
	@ModifyVariable
	(
		method = "getFov",
		at = @At
		(
			value = "FIELD",
			target = "net/minecraft/client/renderer/GameRenderer.oldFov:F",
			shift = Shift.BY,
			by = -3
		),
		ordinal = 0
	)
	private double getFov(double fov)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		IClientConfig config = instance.getClientConfig();
		
		if(instance.isShoulderSurfing() && config.isFovOverrideEnabled())
		{
			return config.getFovOverride();
		}
		
		return fov;
	}
}
