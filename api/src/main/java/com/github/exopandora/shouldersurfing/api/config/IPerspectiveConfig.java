package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.Perspective;

public interface IPerspectiveConfig {
	boolean isThirdPersonReplaced();
	
	boolean isFirstPersonEnabled();
	
	boolean isThirdPersonFrontEnabled();
	
	boolean isThirdPersonBackEnabled();
	
	Perspective getDefaultPerspective();
	
	boolean isPerspectivePersistent();
}
