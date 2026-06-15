package com.github.exopandora.shouldersurfing.api.config;

import java.util.List;

public interface IIntegrationsConfig {
	List<? extends String> getCuriosAdaptiveCrosshairItems();
	
	List<? extends String> getCuriosAdaptiveCrosshairItemProperties();
	
	boolean isEpicFightDecoupledCameraLockOnEnabled();
}
