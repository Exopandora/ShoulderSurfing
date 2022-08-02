package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3f;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoAccessor
{
	@Accessor
	Vector3f getLeft();
	
	@Accessor
	float getEyeHeightOld();
	
	@Accessor
	float getEyeHeight();
	
	@Invoker
	void invokeSetPosition(double x, double y, double z);
	
	@Invoker
	void invokeMove(double x, double y, double z);
	
	@Invoker
	double invokeGetMaxZoom(double distance);
}
