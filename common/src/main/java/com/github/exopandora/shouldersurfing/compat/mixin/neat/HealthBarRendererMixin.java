package com.github.exopandora.shouldersurfing.compat.mixin.neat;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "vazkii.neat.HealthBarRenderer")
public class HealthBarRendererMixin {
	@Inject(
		method = "getEntityLookedAt",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	private static void getEntityLookedAt(Entity cameraEntity, CallbackInfoReturnable<Entity> cir) {
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		if (instance.isShoulderSurfing()) {
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			PickContext pickContext = new PickContext.Builder(camera)
				.withEntity(cameraEntity)
				.build();
			EntityHitResult result = instance.getObjectPicker().pickEntities(pickContext, 32, partialTick);
			cir.setReturnValue(result != null ? result.getEntity() : null);
		}
	}
}
