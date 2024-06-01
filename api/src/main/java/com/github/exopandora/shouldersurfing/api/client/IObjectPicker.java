package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public interface IObjectPicker
{
	RayTraceResult pick(PickContext context, double interactionRange, float partialTick, PlayerController gameMode);
	
	EntityRayTraceResult pickEntities(PickContext context, double interactionRange, float partialTick);
	
	BlockRayTraceResult pickBlocks(PickContext context, double interactionRange, float partialTick);
}
