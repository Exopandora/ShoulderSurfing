package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera
{
	@Shadow
	protected abstract void move(float x, float y, float z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setupRotations(BlockGetter level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick, CallbackInfo ci)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec2f rotations = camera.calcRotations(cameraEntity, partialTick);
			this.setRotation(rotations.y(), rotations.x());
		}
	}
	
	@Redirect
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;move(FFF)V",
			ordinal = 0
		)
	)
	private void setupPosition(Camera cameraIn, float x, float y, float z, BlockGetter level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec3 cameraOffset = camera.calcOffset(cameraIn, level, partialTick, cameraEntity);
			this.move((float) -cameraOffset.z(), (float) cameraOffset.y(), (float) -cameraOffset.x());
		}
		else
		{
			this.move(x, y, z);
		}
	}
}
