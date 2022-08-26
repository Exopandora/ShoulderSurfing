package com.teamderpy.shouldersurfing.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(Item.class)
public class MixinItem
{
	@Redirect
	(
		method = "getPlayerPOVHitResult",
		at = @At
		(
			value = "NEW",
			target = "Lnet/minecraft/util/math/RayTraceContext;"
		)
	)
	@SuppressWarnings("resource")
	private static RayTraceContext initRayTraceContext(Vector3d start, Vector3d end, BlockMode block, FluidMode fluid, @Nullable Entity entity)
	{
		ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getInstance().gameRenderer.getMainCamera(), entity, 1.0F, start.distanceToSqr(end));
		return new RayTraceContext(look.cameraPos(), look.traceEndPos(), block, fluid, entity);
	}
}
