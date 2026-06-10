package com.github.exopandora.shouldersurfing.api.event;

public class CancellableEvent implements Event {
	private boolean cancelled = false;
	
	public boolean isCancelled() {
		return this.cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
}
