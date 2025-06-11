package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/renderer/item/ItemProperties$2")
public class MixinItemProperties$2
{
	@Redirect
	(
		method = "unclampedCall",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/LivingEntity.getYRot()F"
		)
	)
	private float getYRot(LivingEntity entity)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && instance.isCameraDecoupled())
		{
			return instance.getCamera().getYRot();
		}
		
		return entity.getYRot();
	}
}
