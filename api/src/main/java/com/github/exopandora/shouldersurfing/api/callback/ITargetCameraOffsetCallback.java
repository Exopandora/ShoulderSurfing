package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * This callback can be used to implement custom target camera offsets.
 * There are two distinct methods, invoked at different time steps of the camera offset calculation.
 * Both methods provide a default NOP implementation.
 *
 * @since 4.1.0
 */
public interface ITargetCameraOffsetCallback {
	/**
	 * @param context The arguments of this callback.
	 * @return The modified target offset for the camera
	 * @since 5.0.0
	 */
	default Vec3 getTargetOffset(GetTagetCameraOffsetContext context) {
		return context.targetOffset();
	}
	
	record GetTagetCameraOffsetContext(
		IShoulderSurfing instance,
		Vec3 targetOffset,
		Vec3 defaultOffset,
		Camera camera,
		@NotNull Entity cameraEntity,
		BlockGetter level
	) {
	}
}
