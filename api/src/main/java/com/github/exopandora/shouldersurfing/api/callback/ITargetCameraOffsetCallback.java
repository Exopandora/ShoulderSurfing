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
 * @since 4.1.0
 */
public interface ITargetCameraOffsetCallback
{
	/**
	 * @deprecated Planned to be removed in 26.3.
	 * Use <code>ITargetCameraOffsetCallback.getTargetOffset</code> instead.
	 * The order can be controlled by setting the event priority on callback registration.
	 * @param instance The IShoulderSurfing instance
	 * @param targetOffset The target offset for the camera
	 * @param defaultOffset The default offset for the camera, without any prior modifications
	 * @return The modified target offset for the camera
	 * @since 4.1.0
	 */
	@Deprecated(since = "4.23", forRemoval = true)
	default Vec3 pre(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		return targetOffset;
	}
	
	/**
	 * @deprecated Planned to be removed in 26.3.
	 * Use <code>ITargetCameraOffsetCallback.getTargetOffset</code> instead.
	 * The order can be controlled by setting the event priority on callback registration.
	 * @param instance The IShoulderSurfing instance
	 * @param targetOffset The target offset for the camera, after offset multipliers have been applied
	 * @param defaultOffset The default offset for the camera, without any prior modifications
	 * @return The modified target offset for the camera
	 * @since 4.1.0
	 */
	@Deprecated(since = "4.23", forRemoval = true)
	default Vec3 post(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset)
	{
		return targetOffset;
	}
	
	/**
	 * @param context The arguments of this callback.
	 * @return The modified target offset for the camera
	 * @since 4.1.0
	 */
	default Vec3 getTargetOffset(Context context)
	{
		return context.targetOffset();
	}
	
	record Context(IShoulderSurfing instance, Vec3 targetOffset, Vec3 defaultOffset, Camera camera, @NotNull Entity cameraEntity, BlockGetter level)
	{
	}
}
