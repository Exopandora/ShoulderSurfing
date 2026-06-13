package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.config.ICameraConfig;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.PlayerConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShoulderSurfingCamera implements IShoulderSurfingCamera {
	private final ShoulderSurfing instance;
	private Vec3 offset;
	private Vec3 offsetO;
	private Vec3 renderOffset;
	private Vec3 targetOffset;
	private Vec3 deltaMovementO;
	private double cameraDistance;
	private double maxCameraDistance;
	private double maxCameraDistanceO;
	private Vec2f rotation;
	private Vec2f rotationO; // only used for followPlayerRotations
	private Vec2f rotationOffset;
	private Vec2f rotationOffsetO;
	private Vec2f renderRotation;
	private float lastMovedYRot;
	private boolean initialized;
	private int turnCameraWithPlayerDelay;
	private float turnCameraWithPlayerEaseIn;
	private float turnCameraWithPlayerEaseInO;
	
	public ShoulderSurfingCamera(ShoulderSurfing instance) {
		this.instance = instance;
		this.init();
	}
	
	private void init() {
		this.offset = Config.CLIENT.getCameraConfig().getOffset();
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		if (cameraEntity != null) {
			this.offset = this.offset.scale(EntityHelper.getScale(cameraEntity));
			this.rotation = new Vec2f(cameraEntity.getXRot(), cameraEntity.getYRot());
			this.deltaMovementO = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		} else {
			this.rotation = new Vec2f(0F, -180F);
			this.deltaMovementO = Vec3.ZERO;
		}
		this.rotationO = this.rotation;
		this.offsetO = this.offset;
		this.renderOffset = this.offset;
		this.targetOffset = this.offset;
		this.maxCameraDistance = this.offset.length();
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.renderRotation = this.rotation;
		this.rotationOffset = Vec2f.ZERO;
		this.rotationOffsetO = Vec2f.ZERO;
		this.lastMovedYRot = this.rotation.y();
		this.turnCameraWithPlayerDelay = 0;
		this.turnCameraWithPlayerEaseIn = 1.0F;
		this.turnCameraWithPlayerEaseInO = 1.0F;
		this.initialized = true;
	}
	
	public void tick() {
		if (!this.initialized) {
			this.init();
		}
		ICameraConfig cameraConfig = Config.CLIENT.getCameraConfig();
		double cameraTransitionSpeedMultiplier = cameraConfig.getCameraTransitionSpeedMultiplier();
		this.rotationO = this.rotation;
		this.rotationOffsetO = this.rotationOffset;
		this.offsetO = this.offset;
		this.offset = this.offsetO.lerp(this.targetOffset, cameraTransitionSpeedMultiplier);
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.maxCameraDistance = this.maxCameraDistance + (this.offset.length() - this.maxCameraDistance) * cameraTransitionSpeedMultiplier;
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		if (this.shouldResetCameraTurningWithPlayerDelay()) {
			this.turnCameraWithPlayerDelay = cameraConfig.getCameraTurningWithPlayerDelay();
			this.turnCameraWithPlayerEaseIn = 1.0F;
			this.turnCameraWithPlayerEaseInO = 1.0F;
		} else if (this.turnCameraWithPlayerDelay == 0) {
			this.turnCameraWithPlayerEaseInO = this.turnCameraWithPlayerEaseIn;
			this.turnCameraWithPlayerEaseIn *= 1F - (float) cameraConfig.getCameraTransitionSpeedMultiplier();
		} else if (this.turnCameraWithPlayerDelay > 0) {
			this.turnCameraWithPlayerDelay--;
		}
		this.rotation = this.applyPassengerRotations(this.rotation, cameraEntity, 1.0F);
		if (cameraEntity != null) {
			this.deltaMovementO = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		}
		if (!this.instance.isFreeLooking()) {
			this.rotationOffset = this.rotationOffset.scale(0.5F);
		}
	}
	
	public void renderTick(Entity cameraEntity, float partialTick) {
		if (!this.instance.isShoulderSurfing()) {
			return;
		}
		if (this.isCameraTurningWithPlayer()) {
			float easeIn = 1F - Mth.lerp(partialTick, this.turnCameraWithPlayerEaseInO, this.turnCameraWithPlayerEaseIn);
			float f = partialTick * (float) Config.CLIENT.getCameraConfig().getCameraTransitionSpeedMultiplier() * easeIn;
			float dy = Mth.degreesDifference(this.rotation.y(), cameraEntity.getYRot(partialTick));
			this.rotation = this.rotationO.add(new Vec2f(0, dy).scale(f));
		}
		if (!this.instance.isCameraDecoupled() && EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living) {
			this.renderRotation = new Vec2f(living.getViewXRot(partialTick), living.getViewYRot(partialTick));
		} else {
			Vec2f rotationOffset = this.rotationOffsetO.rotLerp(this.rotationOffset, partialTick);
			Vec2f rotation = this.rotation.add(rotationOffset).clampX(-90F, 90F);
			this.renderRotation = this.applyPassengerRotations(rotation, cameraEntity, partialTick);
		}
	}
	
	public void setup(Camera camera, BlockGetter level, float partialTick, Entity cameraEntity) {
		Vec3 defaultOffset = Config.CLIENT.getCameraConfig().getOffset();
		this.targetOffset = EventHooks.getTargetOffset(defaultOffset, camera, cameraEntity, level);
		Vec3 drag = this.calcCameraDrag(camera, cameraEntity, partialTick);
		Vec3 lerpedOffset = this.offsetO.lerp(this.offset, partialTick).add(drag);
		if (cameraEntity.isSpectator()) {
			this.cameraDistance = lerpedOffset.length();
			this.renderOffset = lerpedOffset;
		} else {
			double targetCameraDistance = maxZoom(camera, cameraEntity, level, lerpedOffset, partialTick);
			if (targetCameraDistance < this.maxCameraDistance) {
				this.maxCameraDistance = targetCameraDistance;
			}
			double lerpedMaxDistance = Mth.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance);
			this.cameraDistance = Math.min(targetCameraDistance, lerpedMaxDistance);
			this.renderOffset = lerpedOffset.normalize().scale(this.cameraDistance);
		}
	}
	
	private Vec2f applyPassengerRotations(Vec2f rotation, @Nullable Entity cameraEntity, float partialTick) {
		if (this.instance.isCameraDecoupled()) {
			if (EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living) {
				float dx = living.getXRot() - living.xRotO;
				float dy = living.getYHeadRot() - living.yHeadRotO;
				return rotation.add(new Vec2f(dx, dy).scale(partialTick));
			}
		} else if (isCameraTurningWithVehicle(cameraEntity)) {
			Entity vehicle = cameraEntity.getVehicle();
			if (vehicle != null) {
				return rotation.add(0, (vehicle.getYRot() - vehicle.yRotO) * partialTick);
			}
		}
		return rotation;
	}
	
	private static double maxZoom(Camera camera, Entity cameraEntity, BlockGetter level, Vec3 cameraOffset, float partialTick) {
		double distance = cameraOffset.length();
		Vec3 worldOffset = new Vec3(camera.upVector()).scale(cameraOffset.y())
			.add(new Vec3(camera.leftVector()).scale(cameraOffset.x()))
			.add(new Vec3(camera.forwardVector()).scale(-cameraOffset.z()));
		Vec3 eyePosition = cameraEntity.getEyePosition(partialTick);
		for (int i = 0; i < 8; i++) {
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1);
			Vec3 fromOffset = offset.scale(Math.clamp(cameraEntity.getBbWidth() / 2.0F / Mth.sqrt(2), 0.0F, 0.15F))
				.xRot(-camera.xRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.yRot() * Mth.DEG_TO_RAD);
			Vec3 from = eyePosition.add(fromOffset);
			Vec3 toOffset = offset.scale(0.15)
				.xRot(-camera.xRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.yRot() * Mth.DEG_TO_RAD);
			Vec3 to = eyePosition.add(toOffset).add(worldOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, cameraEntity);
			HitResult hitResult = level.clip(context);
			if (hitResult.getType() != HitResult.Type.MISS) {
				double newDistance = hitResult.getLocation().distanceTo(eyePosition);
				if (newDistance < distance) {
					distance = newDistance;
				}
			}
		}
		return distance;
	}
	
	private Vec3 calcCameraDrag(Camera cameraIn, Entity cameraEntity, float partialTick) {
		Vec3 deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.multiply(Config.CLIENT.getCameraConfig().getCameraDragMultipliers())
			.yRot(cameraIn.yRot() * Mth.DEG_TO_RAD)
			.xRot(cameraIn.xRot() * Mth.DEG_TO_RAD);
		return new Vec3(-deltaMovementLerped.x, -deltaMovementLerped.y, deltaMovementLerped.z);
	}
	
	public Vec2f calcSway(Entity cameraEntity, float partialTick) {
		Vec3 deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.yRot(this.getYRot() * Mth.DEG_TO_RAD)
			.xRot(this.getXRot() * Mth.DEG_TO_RAD);
		ICameraConfig cameraConfig = Config.CLIENT.getCameraConfig();
		double maxVelocityX = cameraConfig.getCameraSwayXMaxVelocity() / 20;
		double maxVelocityZ = cameraConfig.getCameraSwayZMaxVelocity() / 20;
		double maxAngleX = cameraConfig.getCameraSwayXMaxAngle();
		double maxAngleZ = cameraConfig.getCameraSwayZMaxAngle();
		double swayX = Math.min(Math.abs(deltaMovementLerped.y), maxVelocityX) / maxVelocityX * maxAngleX * Math.signum(deltaMovementLerped.y);
		double swayZ = Math.min(Math.abs(deltaMovementLerped.x), maxVelocityZ) / maxVelocityZ * maxAngleZ * Math.signum(deltaMovementLerped.x);
		return new Vec2f((float) swayX, (float) swayZ);
	}
	
	public boolean turn(LocalPlayer player, double yRot, double xRot) {
		if (!this.instance.isShoulderSurfing()) {
			return false;
		}
		if (yRot != 0.0F || xRot != 0.0F || EntityHelper.isPlayerSpectatingEntity()) {
			this.turnCameraWithPlayerDelay = Config.CLIENT.getCameraConfig().getCameraTurningWithPlayerDelay();
			this.turnCameraWithPlayerEaseIn = 1.0F;
			this.turnCameraWithPlayerEaseInO = 1.0F;
		}
		Vec2f dRot = new Vec2f((float) xRot, (float) yRot);
		Vec2f dRotScaled = dRot.scale(0.15F);
		if (this.instance.isFreeLooking()) {
			this.rotationOffset = this.rotationOffset.add(dRotScaled).clampX(-90F, 90F);
			this.rotationOffsetO = this.rotationOffset;
			return true;
		}
		Vec2f cameraRot = this.rotation.add(dRotScaled).clampX(-90F, 90F);
		this.rotation = EventHooks.setupCameraRotation(player, cameraRot, this.rotation, dRot, dRotScaled);
		boolean isMoving = player.input.getMoveVector().x != 0.0F || player.input.getMoveVector().y != 0.0F || player.isFallFlying();
		if (this.instance.isCameraDecoupled()) {
			if (!this.instance.isLookFollowingCrosshairTarget()) {
				this.turnPlayerWithCamera(player, dRotScaled, isMoving);
			}
			if (isMoving) {
				this.lastMovedYRot = player.getYRot();
			}
		}
		return this.instance.isCameraDecoupled();
	}
	
	private void turnPlayerWithCamera(LocalPlayer player, Vec2f scaledRot, boolean isMoving) {
		PlayerConfig playerConfig = Config.CLIENT.getPlayerConfig();
		if (playerConfig.isPlayerXRotTurningWithCamera()) {
			player.setXRot(this.rotation.x());
			player.xRotO += Mth.degreesDifference(this.rotation.x(), this.rotation.x());
		}
		if (playerConfig.isPlayerYRotTurningWithCamera() && !isMoving) {
			float maxFollowAngle = (float) playerConfig.getPlayerYRotTurnAngleLimit();
			float playerYRot = Mth.approachDegrees(this.lastMovedYRot, player.getYRot() + scaledRot.y(), maxFollowAngle);
			player.yRotO = player.getYRot();
			player.setYRot(playerYRot);
		}
	}
	
	private boolean shouldResetCameraTurningWithPlayerDelay() {
		if (this.instance.isFreeLooking()) {
			return true;
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && minecraft.player.isScoping()) {
			return true;
		}
		if (minecraft.screen != null) {
			return true;
		}
		if (EntityHelper.isPlayerSpectatingEntity()) {
			return true;
		}
		return this.instance.isLookFollowingCrosshairTarget();
	}
	
	private boolean isCameraTurningWithPlayer() {
		return this.instance.isCameraDecoupled() && Config.CLIENT.getCameraConfig().isCameraTurningWithPlayer() && this.turnCameraWithPlayerDelay == 0;
	}
	
	private static boolean isCameraTurningWithVehicle(@Nullable Entity entity) {
		if (!(entity instanceof LivingEntity)) {
			return false;
		}
		Entity vehicle = entity.getVehicle();
		if (vehicle == null) {
			return false;
		}
		return EventHooks.isRidingBoat((LivingEntity) entity, vehicle);
	}
	
	public void resetState() {
		this.initialized = false;
	}
	
	@Override
	public double getCameraDistance() {
		return this.cameraDistance;
	}
	
	@Override
	public Vec3 getOffset() {
		return this.offset;
	}
	
	@Override
	public Vec3 getRenderOffset() {
		return this.renderOffset;
	}
	
	@Override
	public Vec3 getTargetOffset() {
		return this.targetOffset;
	}
	
	@Override
	public float getXRot() {
		return this.rotation.x() + this.rotationOffset.x();
	}
	
	@Override
	public void setXRot(float xRot) {
		this.rotation = new Vec2f(0, this.rotation.y());
		this.rotationOffset = new Vec2f(0, this.rotationOffset.y());
		this.rotationOffsetO = new Vec2f(0, this.rotationOffsetO.y());
	}
	
	@Override
	public float getYRot() {
		return this.rotation.y() + this.rotationOffset.y();
	}
	
	@Override
	public void setYRot(float yRot) {
		this.rotation = new Vec2f(this.rotation.x(), 0);
		this.rotationOffset = new Vec2f(this.rotationOffset.x(), 0);
		this.rotationOffsetO = new Vec2f(this.rotationOffsetO.x(), 0);
	}
	
	@Override
	public Vec2f getRenderRotation() {
		return this.renderRotation;
	}
	
	public void setLastMovedYRot(float lastMovedYRot) {
		this.lastMovedYRot = lastMovedYRot;
	}
	
	public boolean isLookingUp() {
		return this.getXRot() < Config.CLIENT.getPlayerConfig().getHidePlayerWhenLookingUpAngle() - 90;
	}
	
	public boolean isInsideEntity(Entity cameraEntity) {
		return this.getCameraDistance() < cameraEntity.getBbWidth() * Config.CLIENT.getCameraConfig().keepCameraOutOfHeadMultiplier();
	}
}
