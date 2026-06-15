package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CompassItemPropertyFunction.class)
public class CompassItemPropertyFunctionMixin {
	@Redirect(
		method = "getWrappedVisualRotationY",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/world/entity/Entity.getVisualRotationYInDegrees()F"
		)
	)
	private float getVisualRotationYInDegrees(Entity entity) {
		IShoulderSurfing instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing() && instance.isCameraDecoupled()) {
			return instance.getCamera().getYRot();
		}
		return entity.getVisualRotationYInDegrees();
	}
}
