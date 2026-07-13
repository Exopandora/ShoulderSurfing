package com.github.exopandora.shouldersurfing.compat.mixin.theoneprobe;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "mcjty.theoneprobe.rendering.OverlayRenderer")
class OverlayRendererMixin {
	@Redirect(
		method = "renderHUD",
		at = @At(
			value = "NEW",
			target = "Lnet/minecraft/world/level/ClipContext;",
			remap = true
		),
		remap = false
	)
	private static ClipContext initClipContext(
		Vec3 start,
		Vec3 end,
		ClipContext.Block blockContext,
		ClipContext.Fluid fluidContext,
		@NotNull Entity entity
	) {
		if (IShoulderSurfing.getInstance().isShoulderSurfing()) {
			var minecraft = Minecraft.getInstance();
			var camera = minecraft.gameRenderer.mainCamera();
			var pickContext = new PickContext.Builder(camera)
				.withFluidContext(fluidContext)
				.withEntity(entity)
				.build();
			var partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			return pickContext.toClipContext(start.distanceTo(end), partialTick);
		}
		return new ClipContext(start, end, blockContext, fluidContext, entity);
	}
}
