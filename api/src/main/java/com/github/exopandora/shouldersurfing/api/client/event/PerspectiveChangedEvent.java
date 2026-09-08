package com.github.exopandora.shouldersurfing.api.client.event;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.event.Event;

/**
 * This event can be used to observe perspective changes.
 *
 * @since 5.1.0
 */
public class PerspectiveChangedEvent implements Event {
	private final Perspective perspective;
	
	public PerspectiveChangedEvent(Perspective perspective) {
		this.perspective = perspective;
	}
	
	public Perspective getPerspective() {
		return this.perspective;
	}
}
