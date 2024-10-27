package com.github.exopandora.shouldersurfing.api.client;

import net.minecraft.entity.Entity;

public interface ICrosshairRenderer
{
	boolean doRenderCrosshair();
	
	boolean doRenderObstructionCrosshair();
	
	boolean doRenderObstructionIndicator();
	
	boolean isCrosshairDynamic(Entity entity);
}
