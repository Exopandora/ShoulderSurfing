package com.github.exopandora.shouldersurfing.api.client.config;

import java.util.List;

public interface IIntegrationsConfig {
	List<? extends String> getCuriosAdaptiveCrosshairItems();
	
	List<? extends String> getCuriosAdaptiveCrosshairDefaultItemComponents();
	
	List<? extends String> getCuriosAdaptiveCrosshairItemComponents();
}
