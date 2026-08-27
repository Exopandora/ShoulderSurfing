package com.github.exopandora.shouldersurfing.compat.mixin.neat;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "vazkii.neat.NeatRenderStateHandler")
class NeatRenderStateHandlerMixin {
	@Inject(
		method = "getEntityLookedAt",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	private static void getEntityLookedAt(Entity cameraEntity, CallbackInfoReturnable<Entity> cir) {
		var instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing()) {
			var minecraft = Minecraft.getInstance();
			var camera = minecraft.gameRenderer.mainCamera();
			var partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			var pickContext = new PickContext.Builder(camera)
				.withEntity(cameraEntity)
				.build();
			var result = instance.getObjectPicker().pickEntities(pickContext, 32, partialTick);
			cir.setReturnValue(result != null ? result.getEntity() : null);
		}
	}
}
