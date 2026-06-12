package com.github.exopandora.shouldersurfing.client.event.handler;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.event.ComputeTargetCameraOffsetEvent;
import com.github.exopandora.shouldersurfing.api.client.event.handler.ComputeTargetCameraOffsetEventHandler;
import com.github.exopandora.shouldersurfing.api.config.ICameraConfig;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ComputeTargetCameraOffsetEventHandlerImpl {
	public enum CameraDistanceAttribute implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			if (event.getCameraEntity() instanceof LivingEntity living) {
				event.setResult(applyCameraDistanceAttribute(event.getResult(), living.getAttributeValue(Attributes.CAMERA_DISTANCE)));
			}
		}
	}
	
	public enum CameraDistanceAttributePassenger implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			if (event.getCameraEntity().isPassenger() && event.getCameraEntity().getVehicle() instanceof LivingEntity living) {
				event.setResult(applyCameraDistanceAttribute(event.getResult(), living.getAttributeValue(Attributes.CAMERA_DISTANCE)));
			}
		}
	}
	
	private interface AbstractModifiersAndMultipliers extends ComputeTargetCameraOffsetEventHandler {
		@Override
		default void handle(ComputeTargetCameraOffsetEvent event) {
			if (this.shouldApply(event)) {
				Vec3 result = event.getResult()
					.add(event.getDefaultOffset().multiply(this.getMultipliers()).subtract(event.getDefaultOffset()))
					.add(this.getModifiers());
				event.setResult(result);
			}
		}
		
		boolean shouldApply(ComputeTargetCameraOffsetEvent event);
		
		Vec3 getModifiers();
		
		Vec3 getMultipliers();
	}
	
	public enum PassengerModifiersAndMultipliers implements AbstractModifiersAndMultipliers {
		INSTANCE;
		
		@Override
		public boolean shouldApply(ComputeTargetCameraOffsetEvent event) {
			return event.getCameraEntity().isPassenger();
		}
		
		@Override
		public Vec3 getModifiers() {
			return Config.CLIENT.getCameraConfig().getPassengerOffsetModifiers();
		}
		
		@Override
		public Vec3 getMultipliers() {
			return Config.CLIENT.getCameraConfig().getPassengerOffsetMultipliers();
		}
	}
	
	public enum SprintingModifiersAndMultipliers implements AbstractModifiersAndMultipliers {
		INSTANCE;
		
		@Override
		public boolean shouldApply(ComputeTargetCameraOffsetEvent event) {
			return event.getCameraEntity().isSprinting();
		}
		
		@Override
		public Vec3 getModifiers() {
			return Config.CLIENT.getCameraConfig().getSprintOffsetModifiers();
		}
		
		@Override
		public Vec3 getMultipliers() {
			return Config.CLIENT.getCameraConfig().getSprintOffsetMultipliers();
		}
	}
	
	public enum AimingModifiersAndMultipliers implements AbstractModifiersAndMultipliers {
		INSTANCE;
		
		@Override
		public boolean shouldApply(ComputeTargetCameraOffsetEvent event) {
			return IShoulderSurfing.getInstance().isAiming();
		}
		
		@Override
		public Vec3 getModifiers() {
			return Config.CLIENT.getCameraConfig().getAimingOffsetModifiers();
		}
		
		@Override
		public Vec3 getMultipliers() {
			return Config.CLIENT.getCameraConfig().getAimingOffsetMultipliers();
		}
	}
	
	public enum FallFlyingModifiersAndMultipliers implements AbstractModifiersAndMultipliers {
		INSTANCE;
		
		@Override
		public boolean shouldApply(ComputeTargetCameraOffsetEvent event) {
			return event.getCameraEntity() instanceof LivingEntity living && living.isFallFlying();
		}
		
		@Override
		public Vec3 getModifiers() {
			return Config.CLIENT.getCameraConfig().getFallFlyingOffsetModifiers();
		}
		
		@Override
		public Vec3 getMultipliers() {
			return Config.CLIENT.getCameraConfig().getFallFlyingMultipliers();
		}
	}
	
	public enum ClimbingModifiersAndMultipliers implements AbstractModifiersAndMultipliers {
		INSTANCE;
		
		@Override
		public boolean shouldApply(ComputeTargetCameraOffsetEvent event) {
			return !event.getCameraEntity().isSpectator() && event.getCameraEntity() instanceof LivingEntity living && living.onClimbable();
		}
		
		@Override
		public Vec3 getModifiers() {
			return Config.CLIENT.getCameraConfig().getClimbingOffsetModifiers();
		}
		
		@Override
		public Vec3 getMultipliers() {
			return Config.CLIENT.getCameraConfig().getClimbingMultipliers();
		}
	}
	
	public enum CenterWhenLookingDown implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			if (!event.getCameraEntity().isSpectator() && isCameraLookingDown(event.getCamera())) {
				event.setResult(new Vec3(0, 0, event.getResult().z()));
			}
		}
		
		private boolean isCameraLookingDown(Camera camera) {
			return camera.forwardVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCameraConfig().getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD;
		}
	}
	
	public enum DynamicOffsets implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			if (!event.getCameraEntity().isSpectator() && Config.CLIENT.getCameraConfig().isOffsetDynamic()) {
				event.setResult(calcDynamicOffsets(event.getCamera(), event.getCameraEntity(), event.getLevel(), event.getResult()));
			}
		}
		
		private static Vec3 calcDynamicOffsets(Camera camera, Entity cameraEntity, BlockGetter level, Vec3 targetOffset) {
			Vec3 lookVector = new Vec3(camera.forwardVector());
			Vec3 worldXYOffset = new Vec3(camera.upVector()).scale(targetOffset.y())
				.add(new Vec3(camera.leftVector()).scale(targetOffset.x()));
			Vec3 worldOffset = worldXYOffset.add(lookVector.scale(-targetOffset.z()));
			double offsetXAbs = Math.abs(targetOffset.x());
			double offsetYAbs = Math.abs(targetOffset.y());
			double offsetZAbs = Math.abs(targetOffset.z());
			double targetX = offsetXAbs;
			double targetY = offsetYAbs;
			double clearance = cameraEntity.getBbWidth() / 3.0D;
			Vec3 cameraPosition = camera.position();
			for (double dz = 0; dz <= offsetZAbs; dz += 0.03125D) {
				double scale = dz / offsetZAbs;
				Vec3 startPos = cameraPosition.add(worldOffset.scale(scale));
				Vec3 endPos = cameraPosition.add(worldXYOffset).add(lookVector.scale(-dz));
				ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, cameraEntity);
				HitResult hitResult = level.clip(context);
				if (hitResult.getType() != HitResult.Type.MISS) {
					double distance = hitResult.getLocation().distanceTo(startPos);
					double newTargetX = Math.max(distance + offsetXAbs * scale - clearance, 0);
					if (newTargetX < targetX) {
						targetX = newTargetX;
					}
					double newTargetY = Math.max(distance + offsetYAbs * scale - clearance, 0);
					if (newTargetY < targetY) {
						targetY = newTargetY;
					}
				}
			}
			double targetXOffset = Math.signum(targetOffset.x()) * targetX;
			double targetYOffset = Math.signum(targetOffset.y()) * targetY;
			return new Vec3(targetXOffset, targetYOffset, targetOffset.z());
		}
	}
	
	public enum OffsetLimits implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			ICameraConfig cameraConfig = Config.CLIENT.getCameraConfig();
			double targetOffsetX = cameraConfig.isOffsetXUnlimited()
				? event.getResult().x()
				: Mth.clamp(event.getResult().x(), cameraConfig.getMinOffsetX(), cameraConfig.getMaxOffsetX());
			double targetOffsetY = cameraConfig.isOffsetYUnlimited()
				? event.getResult().y()
				: Mth.clamp(event.getResult().y(), cameraConfig.getMinOffsetY(), cameraConfig.getMaxOffsetY());
			double targetOffsetZ = cameraConfig.isOffsetZUnlimited()
				? event.getResult().z()
				: Mth.clamp(event.getResult().z(), cameraConfig.getMinOffsetZ(), cameraConfig.getMaxOffsetZ());
			event.setResult(new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ));
		}
	}
	
	public enum EntityScale implements ComputeTargetCameraOffsetEventHandler {
		INSTANCE;
		
		@Override
		public void handle(ComputeTargetCameraOffsetEvent event) {
			event.setResult(event.getResult().scale(EntityHelper.getMaxScale(event.getCameraEntity())));
		}
	}
	
	private static Vec3 applyCameraDistanceAttribute(Vec3 targetVec, double cameraDistance) {
		return switch (Config.CLIENT.getCameraConfig().getCameraDistanceAttributeMode()) {
			case RELATIVE -> targetVec.multiply(1.0D, 1.0D, cameraDistance / 4.0D);
			case ABSOLUTE -> new Vec3(targetVec.x, targetVec.y, cameraDistance);
			case IGNORE -> targetVec;
		};
	}
}
