package com.teamderpy.shouldersurfing.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;

@Mixin(Item.class)
public class MixinItem
{
	@Redirect
	(
		method = "getPlayerPOVHitResult",
		at = @At
		(
			value = "NEW",
			target = "Lnet/minecraft/world/level/ClipContext;"
		)
	)
	@SuppressWarnings("resource")
	private static ClipContext initClipContext(Vec3 start, Vec3 end, Block block, Fluid fluid, @Nullable Entity entity)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(Minecraft.getInstance().gameRenderer.getMainCamera(), entity, 1.0F, start.distanceToSqr(end));
			return new ClipContext(look.cameraPos(), look.traceEndPos(), block, fluid, entity);
		}
		
		return new ClipContext(start, end, block, fluid, entity);
	}
}
