package com.github.exopandora.shouldersurfing.api.config;

import com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickVector;

import java.util.List;

public interface IObjectPickerConfig {
	double getCustomRaytraceDistance();
	
	boolean isCustomRaytraceDistanceEnabled();
	
	List<? extends String> getUnpickableInvisibleEntities();
	
	PickOrigin getEntityPickOrigin();
	
	PickOrigin getBlockPickOrigin();
	
	PickVector getPickVector();
}
