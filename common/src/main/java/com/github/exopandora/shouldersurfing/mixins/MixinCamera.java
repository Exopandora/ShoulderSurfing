package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera implements CameraDuck
{
	@Shadow
	private @Nullable Level level;
	
	@Shadow
	private @Nullable Entity entity;
	
	@Shadow
	private float xRot;
	
	@Shadow
	private float yRot;
	
	@Unique
	private float zRot;
	
	@Shadow
	protected abstract void move(float x, float y, float z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject
	(
		method = "alignWithEntity",
		at = @At("HEAD")
	)
	private void setupRotations(CallbackInfo ci)
	{
		this.shouldersurfing$setZRot(0.0F);
	}
	
	@Inject
	(
		method = "alignWithEntity",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setupRotations(float partialTick, CallbackInfo ci)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(this.entity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec2f rotations = camera.calcRotations(this.entity, partialTick);
			this.setRotation(rotations.y(), rotations.x());
		}
	}
	
	@Redirect
	(
		method = "alignWithEntity",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;move(FFF)V",
			ordinal = 0
		)
	)
	private void setupPosition(Camera cameraIn, float x, float y, float z, float partialTick)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(this.entity instanceof LivingEntity livingEntity && livingEntity.isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec3 cameraOffset = camera.calcOffset(cameraIn, this.level, partialTick, this.entity);
			this.move((float) -cameraOffset.z(), (float) cameraOffset.y(), (float) -cameraOffset.x());
			Vec2f sway = camera.calcSway(camera, this.entity, partialTick);
			this.zRot = sway.y();
			this.setRotation(this.yRot, this.xRot + sway.x());
		}
		else
		{
			this.move(x, y, z);
		}
	}
	
	@ModifyVariable
	(
		method = "calculateFov",
		at = @At
		(
			value = "TAIL",
			shift = Shift.BY,
			by = -2
		),
		ordinal = 1
	)
	private float calculateFov(float lerpedFov)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		IClientConfig config = instance.getClientConfig();
		
		if(instance.isShoulderSurfing() && config.isFovOverrideEnabled())
		{
			return (config.getFovOverride() / (float) Minecraft.getInstance().options.fov().get()) * lerpedFov;
		}
		
		return lerpedFov;
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
