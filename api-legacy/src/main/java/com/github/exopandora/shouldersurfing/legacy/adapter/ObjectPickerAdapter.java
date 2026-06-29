package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

class ObjectPickerAdapter implements IObjectPicker {
	private final com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker objectPicker;
	
	protected ObjectPickerAdapter(com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker objectPicker) {
		this.objectPicker = objectPicker;
	}
	
	@Override
	public HitResult pick(PickContext context, double interactionRange, float partialTick, Player player) {
		return this.objectPicker.pick(context.toNewApi(), interactionRange, partialTick, player);
	}
	
	@Override
	public EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick) {
		return this.objectPicker.pickEntities(context.toNewApi(), interactionRange, partialTick);
	}
	
	@Override
	public BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick) {
		return this.objectPicker.pickBlocks(context.toNewApi(), interactionRange, partialTick);
	}
}
