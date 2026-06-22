package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
public abstract class CameraMixin implements CameraDuck {
	@Shadow
	private @Final Vector3f forwards;
	
	@Shadow
	private @Final Vector3f up;
	
	@Shadow
	private @Final Vector3f left;
	
	@Shadow
	private float xRot;
	
	@Shadow
	private float yRot;
	
	@Unique
	private float zRot;
	
	@Shadow
	private @Final Quaternionf rotation;
	
	@Shadow
	protected abstract void move(double x, double y, double z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject(
		method = "setup",
		at = @At("HEAD")
	)
	private void setupRotations(CallbackInfo ci) {
		this.shouldersurfing$setZRot(0.0F);
	}
	
	@Inject(
		method = "setup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setupRotations(
		BlockGetter level,
		Entity cameraEntity,
		boolean detached,
		boolean isMirrored,
		float partialTick,
		CallbackInfo ci
	) {
		if (Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping())) {
			Vec2f rotation = ShoulderSurfing.getInstance().getCamera().getRenderRotation();
			this.setRotation(rotation.y(), rotation.x());
		}
	}
	
	@Redirect(
		method = "setup",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;move(DDD)V",
			ordinal = 0
		)
	)
	private void setupPosition(
		Camera cameraIn,
		double x,
		double y,
		double z,
		BlockGetter level,
		Entity cameraEntity,
		boolean detached,
		boolean isMirrored,
		float partialTick
	) {
		if (Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity livingEntity && livingEntity.isSleeping())) {
			ShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
			camera.setup(cameraIn, level, partialTick, cameraEntity);
			Vec3 cameraOffset = camera.getRenderOffset();
			this.move(-cameraOffset.z(), cameraOffset.y(), cameraOffset.x());
			Vec2f sway = camera.calcSway(cameraEntity, partialTick);
			this.shouldersurfing$rotate(sway.x(), 0, sway.y());
		} else {
			this.move(x, y, z);
		}
	}
	
	@Unique
	private void shouldersurfing$rotate(float xRot, float yRot, float zRot) {
		this.xRot += xRot;
		this.yRot += yRot;
		this.zRot += zRot;
		this.rotation.rotationYXZ(
			-this.yRot * Mth.DEG_TO_RAD,
			this.xRot * Mth.DEG_TO_RAD,
			this.zRot * Mth.DEG_TO_RAD
		);
		this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
		this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
		this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
	}
	
	@Override
	public float shouldersurfing$getZRot() {
		return this.zRot;
	}
	
	@Override
	public void shouldersurfing$setZRot(float zRot) {
		this.zRot = zRot;
	}
}
