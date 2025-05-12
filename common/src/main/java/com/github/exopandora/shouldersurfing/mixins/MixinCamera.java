package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera implements CameraDuck
{
	@Shadow
	private float xRot;
	
	@Shadow
	private float yRot;
	
	@Unique
	private float zRot;
	
	@Shadow
	private @Final Quaternion rotation;
	
	@Shadow
	protected abstract void move(double x, double y, double z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject
	(
		method = "setup",
		at = @At("HEAD")
	)
	private void setupRotations(CallbackInfo ci)
	{
		this.shouldersurfing$setZRot(0.0F);
	}
	
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
			target = "Lnet/minecraft/client/Camera;move(DDD)V",
			ordinal = 0
		)
	)
	private void setupPosition(Camera cameraIn, double x, double y, double z, BlockGetter level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec3 cameraOffset = camera.calcOffset(cameraIn, level, partialTick, cameraEntity);
			this.move(-cameraOffset.z(), cameraOffset.y(), cameraOffset.x());
			Vec2f sway = camera.calcSway(camera, cameraEntity, partialTick);
			this.zRot = sway.y();
			this.setRotation(this.yRot, this.xRot + sway.x());
		}
		else
		{
			this.move(x, y, z);
		}
	}
	
	@Inject
	(
		method = "setRotation(FF)V",
		at = @At
		(
			value = "INVOKE",
			target = "Lcom/mojang/math/Quaternion;mul(Lcom/mojang/math/Quaternion;)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setRotation(CallbackInfo ci)
	{
		this.rotation.mul(Vector3f.ZP.rotationDegrees(this.shouldersurfing$getZRot()));
	}
	
	@Override
	public float shouldersurfing$getZRot()
	{
		return this.zRot;
	}
	
	@Override
	public void shouldersurfing$setZRot(float zRot)
	{
		this.zRot = zRot;
	}
}
