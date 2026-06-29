package com.github.exopandora.shouldersurfing.legacy.adapter;

import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import net.minecraft.world.entity.Entity;

class CrosshairRendererAdapter implements ICrosshairRenderer {
	private final com.github.exopandora.shouldersurfing.api.client.renderer.ICrosshairRenderer crosshairRenderer;
	
	protected CrosshairRendererAdapter(com.github.exopandora.shouldersurfing.api.client.renderer.ICrosshairRenderer crosshairRenderer) {
		this.crosshairRenderer = crosshairRenderer;
	}
	
	@Override
	public boolean doRenderCrosshair() {
		return this.crosshairRenderer.isCrosshairVisible();
	}
	
	@Override
	public boolean doRenderObstructionCrosshair() {
		return this.crosshairRenderer.isObstructionCrosshairVisible();
	}
	
	@Override
	public boolean doRenderObstructionIndicator() {
		return this.crosshairRenderer.isObstructionIndicatorVisible();
	}
	
	@Override
	public boolean isCrosshairDynamic(Entity entity) {
		return this.crosshairRenderer.isCrosshairDynamic();
	}
	
	@Override
	public boolean isCrosshairVisible() {
		return this.crosshairRenderer.isCrosshairVisible();
	}
	
	@Override
	public boolean isObstructionCrosshairVisible() {
		return this.crosshairRenderer.isObstructionCrosshairVisible();
	}
	
	@Override
	public boolean isObstructionIndicatorVisible() {
		return this.crosshairRenderer.isObstructionIndicatorVisible();
	}
	
	@Override
	public boolean isCrosshairDynamic() {
		return this.crosshairRenderer.isCrosshairDynamic();
	}
}
