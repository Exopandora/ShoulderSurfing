package com.github.exopandora.shouldersurfing.api.client;

import net.minecraft.world.entity.Entity;

public interface ICrosshairRenderer
{
	boolean doRenderCrosshair();
	
	boolean isCrosshairDynamic(Entity entity);
}
