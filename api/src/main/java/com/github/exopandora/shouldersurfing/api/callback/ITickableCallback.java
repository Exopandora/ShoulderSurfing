package com.github.exopandora.shouldersurfing.api.callback;

/**
 * This interface can be used to extend any callback class to receive client ticks.
 */
public interface ITickableCallback
{
	/**
	 * Invoked every client tick <b>after</b> Shoulder Surfing Reloaded has ticked
	 */
	void tick();
}
