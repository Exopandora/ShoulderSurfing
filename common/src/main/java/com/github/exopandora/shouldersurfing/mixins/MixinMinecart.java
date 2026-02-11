package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecart.class)
public abstract class MixinMinecart extends AbstractMinecart
{
	@Shadow
	private float rotationOffset;
	
	@Shadow
	private float playerRotationOffset;
	
	protected MixinMinecart(EntityType<?> entityType, Level level)
	{
		super(entityType, level);
	}
	
	@Inject
	(
		method = "positionRider",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/vehicle/minecart/AbstractMinecart.positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V",
			shift = Shift.AFTER
		)
	)
	private void positionRider(Entity rider, MoveFunction moveFunction, CallbackInfo ci)
	{
		IShoulderSurfing instance = ShoulderSurfing.getInstance();
		
		if(instance.isShoulderSurfing() && this.level().isClientSide() && rider instanceof Player player)
		{
			if(player.shouldRotateWithMinecart() && AbstractMinecart.useExperimentalMovement(this.level()))
			{
				IShoulderSurfingCamera camera = instance.getCamera();
				float f = (float) Mth.rotLerp(0.5F, this.playerRotationOffset, (double) this.rotationOffset);
				camera.setYRot(camera.getYRot() - (f - this.playerRotationOffset));
			}
		}
	}
}
