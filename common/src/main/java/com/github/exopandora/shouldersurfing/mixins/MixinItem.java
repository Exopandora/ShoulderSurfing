package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
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
			target = "Lnet/minecraft/world/level/ClipContext;"
		)
	)
	private static ClipContext initClipContext(Vec3 start, Vec3 end, ClipContext.Block blockContext, ClipContext.Fluid fluidContext, @NotNull Entity entity)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing() && entity == Minecraft.getInstance().player && entity.level().isClientSide)
		{
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(fluidContext)
				.withEntity(entity)
				.build();
			return pickContext.toClipContext(start.distanceTo(end), minecraft.getFrameTime());
		}
		
		return new ClipContext(start, end, blockContext, fluidContext, entity);
	}
}
