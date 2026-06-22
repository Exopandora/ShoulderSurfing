package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.mixinduck.CameraDuck;
import net.minecraft.client.Camera;
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
	private static @Final Vector3f FORWARDS;
	
	@Shadow
	private static @Final Vector3f UP;
	
	@Shadow
	private static @Final Vector3f LEFT;
	
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
	protected abstract void move(float x, float y, float z);
	
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
			target = "Lnet/minecraft/client/Camera;move(FFF)V",
			ordinal = 0
		)
	)
	private void setupPosition(
		Camera cameraIn,
		float x,
		float y,
		float z,
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
			this.move((float) -cameraOffset.z(), (float) cameraOffset.y(), (float) -cameraOffset.x());
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
			(float) Math.PI - this.yRot * (float) (Math.PI / 180.0),
			-this.xRot * (float) (Math.PI / 180.0),
			-this.zRot * (float) (Math.PI / 180.0)
		);
		FORWARDS.rotate(this.rotation, this.forwards);
		UP.rotate(this.rotation, this.up);
		LEFT.rotate(this.rotation, this.left);
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
