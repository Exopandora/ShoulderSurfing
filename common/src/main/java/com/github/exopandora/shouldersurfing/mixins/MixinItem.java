package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRayTraceContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
	private static RayTraceContext initRayTraceContext(Vector3d start, Vector3d end, BlockMode block, FluidMode fluid, @NotNull Entity entity)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ShoulderRayTraceContext context = ShoulderRayTraceContext.from(minecraft.gameRenderer.getMainCamera(), entity, 1.0F, start.distanceToSqr(end));
			return new RayTraceContext(context.startPos(), context.endPos(), block, fluid, entity);
		}
		
		return new RayTraceContext(start, end, block, fluid, entity);
	}
}
