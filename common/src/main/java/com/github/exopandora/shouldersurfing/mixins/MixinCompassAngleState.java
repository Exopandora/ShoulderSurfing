package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.world.entity.ItemOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CompassAngleState.class)
public class MixinCompassAngleState
{
	@Redirect
	(
		method = "getWrappedVisualRotationY",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/ItemOwner.getVisualRotationYInDegrees()F"
		)
	)
	private static float getVisualRotationYInDegrees(ItemOwner itemOwner)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && instance.isCameraDecoupled())
		{
			return instance.getCamera().getYRot();
		}
		
		return itemOwner.getVisualRotationYInDegrees();
	}
}
