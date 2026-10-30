package com.github.exopandora.shouldersurfing.api.config;

public interface IClientConfig {
	ICameraConfig getCameraConfig();
	
	IPerspectiveConfig getPerspectiveConfig();
	
	IPlayerConfig getPlayerConfig();
	
	IObjectPickerConfig getObjectPickerConfig();
	
	ICrosshairConfig getCrosshairConfig();
	
	IGuiConfig getGuiConfig();
	
	IAudioConfig getAudioConfig();
	
	IIntegrationsConfig getIntegrationsConfig();
}
