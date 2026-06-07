package com.github.exopandora.shouldersurfing.api.client.config;

import com.github.exopandora.shouldersurfing.api.model.PickOrigin;
import com.github.exopandora.shouldersurfing.api.model.PickVector;

public interface IObjectPickerConfig
{
	double getCustomRaytraceDistance();
	
	boolean isCustomRaytraceDistanceEnabled();
	
	PickOrigin getEntityPickOrigin();
	
	PickOrigin getBlockPickOrigin();
	
	PickVector getPickVector();
}
