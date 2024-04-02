package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderHelper;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;
import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
	private static RayTraceContext initRayTraceContext(Vector3d start, Vector3d end, BlockMode block, FluidMode fluid, @NotNull Entity entity)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ShoulderHelper.ShoulderLook look = ShoulderHelper.shoulderSurfingLook(minecraft.gameRenderer.getMainCamera(), entity, 1.0F, start.distanceToSqr(end));
			Vector3d eyePosition = entity.getEyePosition(1.0F);
			Vector3d from = eyePosition.add(look.headOffset());
			Vector3d to = look.traceEndPos();
			return new RayTraceContext(from, to, block, fluid, entity);
		}
		
		return new RayTraceContext(start, end, block, fluid, entity);
	}
}
