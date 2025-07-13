package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.world.entity.Entity;

/**
 * This callback can be used to implement custom camera entity transparency rules.
 * The final result is the minimum value from all partial results.
 */
public interface ICameraEntityTransparencyCallback
{
	/**
	 * @param instance The IShoulderSurfing instance
	 * @param cameraEntity The camera entity
	 * @param partialTick The render partial tick
	 * @return The alpha value to use for camera entity rendering, ranging from <code>0.0F</code> (invisible) to <code>1.0F</code> (opaque)
	 */
	float getCameraEntityAlpha(IShoulderSurfing instance, Entity cameraEntity, float partialTick);
}
