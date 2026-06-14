package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;

public interface IObjectPickerConfig {
	double getCustomRaytraceDistance();
	
	boolean isCustomRaytraceDistanceEnabled();
	
	PickOrigin getEntityPickOrigin();
	
	PickOrigin getBlockPickOrigin();
	
	PickVector getPickVector();
}
