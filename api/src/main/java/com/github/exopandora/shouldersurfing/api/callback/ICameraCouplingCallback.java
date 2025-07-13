package com.github.exopandora.shouldersurfing.api.callback;

import net.minecraft.client.Minecraft;

/**
 * This callback can be used to implement custom camera coupling rules.
 * The final result is calculated from all partial results using a logical OR.
 */
public interface ICameraCouplingCallback
{
	/**
	 *
	 * @param minecraft The Minecraft instance
	 * @return <code>true</code> if the camera should be coupled, <code>false</code> if the camera can be decoupled
	 */
	boolean isForcingCameraCoupling(Minecraft minecraft);
}
