package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.event.CancellableEvent;

/**
 * This event can be used to implement custom camera coupling rules.
 *
 * @since 5.0.0
 */
public class ComputeCameraCouplingEvent extends CancellableEvent {
	private boolean result;
	
	public boolean getResult() {
		return this.result;
	}
	
	public void setResult(boolean result) {
		this.result = result;
	}
}
