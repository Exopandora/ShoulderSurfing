package com.github.exopandora.shouldersurfing.api.client.config;

import com.github.exopandora.shouldersurfing.api.model.Perspective;

public interface IPerspectiveConfig
{
	boolean isThirdPersonReplaced();
	
	boolean isFirstPersonEnabled();
	
	boolean isThirdPersonFrontEnabled();
	
	boolean isThirdPersonBackEnabled();
	
	Perspective getDefaultPerspective();
	
	boolean isPerspectivePersistent();
}
