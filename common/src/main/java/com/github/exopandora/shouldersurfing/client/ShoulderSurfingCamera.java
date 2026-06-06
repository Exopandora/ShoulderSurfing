package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.api.model.CameraDistanceAttributeMode;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class ShoulderSurfingCamera implements IShoulderSurfingCamera
{
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
	private final ShoulderSurfingImpl instance;
	private Vec3 offset;
	private Vec3 offsetO;
	private Vec3 renderOffset;
	private Vec3 targetOffset;
	private Vec3 deltaMovementO;
	private double cameraDistance;
	private double maxCameraDistance;
	private double maxCameraDistanceO;
	private Vec2f rotation;
	private Vec2f rotationO;
	private Vec2f rotationOffset;
	private Vec2f rotationOffsetO;
	private float lastMovedYRot;
	private boolean initialized;
	private int followPlayerRotationsDelay;
	private float followPlayerRotationsEaseIn;
	private float followPlayerRotationsEaseInO;
	
	public ShoulderSurfingCamera(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
		this.init();
	}
	
	public void tick()
	{
		if(!this.initialized)
		{
			this.init();
		}
		
		double cameraTransitionSpeedMultiplier = Config.CLIENT.getCameraTransitionSpeedMultiplier();
		this.rotationO = this.rotation;
		this.rotationOffsetO = this.rotationOffset;
		this.offsetO = this.offset;
		this.offset = this.offsetO.lerp(this.targetOffset, cameraTransitionSpeedMultiplier);
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.maxCameraDistance = this.maxCameraDistance + (this.offset.length() - this.maxCameraDistance) * cameraTransitionSpeedMultiplier;
		
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		
		if(this.instance.isCameraDecoupled())
		{
			if(this.shouldResetFollowPlayerRotationsDelay(minecraft))
			{
				this.followPlayerRotationsDelay = Config.CLIENT.getFollowPlayerRotationsDelay();
				this.followPlayerRotationsEaseIn = 1.0F;
				this.followPlayerRotationsEaseInO = 1.0F;
			}
			else if(this.followPlayerRotationsDelay == 0)
			{
				this.followPlayerRotationsEaseInO = this.followPlayerRotationsEaseIn;
				this.followPlayerRotationsEaseIn *= 1F - (float) Config.CLIENT.getCameraTransitionSpeedMultiplier();
			}
			else if(this.followPlayerRotationsDelay > 0)
			{
				this.followPlayerRotationsDelay--;
			}
			
			if(EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
			{
				this.rotation = this.rotation.add(living.getXRot() - living.xRotO, living.getYHeadRot() - living.yHeadRotO);
			}
		}
		else if(shouldSyncCameraRotationsWithVehicleRotations(minecraft, cameraEntity))
		{
			Entity vehicle = cameraEntity.getVehicle();
			
			if(vehicle != null)
			{
				this.rotation = this.rotation.add(0, vehicle.getYRot() - vehicle.yRotO);
			}
		}
		
		if(cameraEntity != null)
		{
			this.deltaMovementO = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		}
		
		if(!this.instance.isFreeLooking())
		{
			this.rotationOffset = this.rotationOffset.scale(0.5F);
		}
	}
	
	public void renderTick(Entity cameraEntity, float partialTick)
	{
		if(this.instance.isShoulderSurfing() && this.instance.isCameraDecoupled() && Config.CLIENT.getFollowPlayerRotations() && this.followPlayerRotationsDelay == 0 && !EntityHelper.isPlayerSpectatingEntity())
		{
			float easeIn = 1F - Mth.lerp(partialTick, this.followPlayerRotationsEaseInO, this.followPlayerRotationsEaseIn);
			float f = partialTick * (float) Config.CLIENT.getCameraTransitionSpeedMultiplier() * easeIn;
			float dx = Mth.degreesDifference(this.rotation.x(), cameraEntity.getXRot(partialTick));
			float dy = Mth.degreesDifference(this.rotation.y(), cameraEntity.getXRot(partialTick));
			this.rotation = this.rotationO.add(new Vec2f(dx, dy).scale(f));
		}
	}
	
	private void init()
	{
		this.offset = new Vec3(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetZ());
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null)
		{
			this.offset = this.offset.scale(EntityHelper.getScale(cameraEntity));
			this.rotation = new Vec2f(cameraEntity.getXRot(), cameraEntity.getYRot());
			this.deltaMovementO = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		}
		else
		{
			this.rotation = new Vec2f(0F, -180F);
			this.deltaMovementO = Vec3.ZERO;
		}
		
		this.rotationO = this.rotation;
		this.offsetO = this.offset;
		this.renderOffset = this.offset;
		this.targetOffset = this.offset;
		this.maxCameraDistance = this.offset.length();
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.rotationOffset = Vec2f.ZERO;
		this.rotationOffsetO = Vec2f.ZERO;
		this.lastMovedYRot = this.rotation.y();
		this.followPlayerRotationsDelay = 0;
		this.followPlayerRotationsEaseIn = 1.0F;
		this.followPlayerRotationsEaseInO = 1.0F;
		this.initialized = true;
	}
	
	public Vec2f calcRotations(Entity cameraEntity, float partialTick)
	{
		if(!this.instance.isCameraDecoupled() && EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
		{
			return new Vec2f(living.getViewXRot(partialTick), living.getViewYRot(partialTick));
		}
		
		Vec2f cameraRotWithOffset = this.rotationOffsetO.rotLerp(this.rotationOffset, partialTick).add(this.rotation).clampX(-90F, 90F);
		
		if(this.instance.isCameraDecoupled())
		{
			if(EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
			{
				Vec2f livingRotDelta = new Vec2f(living.getXRot() - living.xRotO, living.getYHeadRot() - living.yHeadRotO).scale(partialTick);
				return cameraRotWithOffset.add(livingRotDelta);
			}
		}
		else if(shouldSyncCameraRotationsWithVehicleRotations(Minecraft.getInstance(), cameraEntity))
		{
			Entity vehicle = cameraEntity.getVehicle();
			
			if(vehicle != null)
			{
				return cameraRotWithOffset.add(0, (vehicle.getYRot() - vehicle.yRotO) * partialTick);
			}
		}
		
		return cameraRotWithOffset;
	}
	
	public Vec3 calcOffset(Camera camera, BlockGetter level, float partialTick, Entity cameraEntity)
	{
		Vec3 defaultOffset = new Vec3(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetZ());
		Vec3 targetOffset = defaultOffset;
		List<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = ShoulderSurfingRegistrar.getInstance().getTargetCameraOffsetCallbacks();
		
		for(ITargetCameraOffsetCallback targetCameraOffsetCallback : targetCameraOffsetCallbacks)
		{
			targetOffset = targetCameraOffsetCallback.pre(this.instance, targetOffset, defaultOffset);
		}
		
		if(cameraEntity instanceof LivingEntity living)
		{
			targetOffset = applyCameraDistanceAttribute(
				targetOffset,
				living.getAttributeValue(Attributes.CAMERA_DISTANCE),
				Config.CLIENT.getCameraDistanceAttributeMode()
			);
		}
		
		if(cameraEntity.isPassenger())
		{
			if(cameraEntity.getVehicle() instanceof LivingEntity living)
			{
				targetOffset = applyCameraDistanceAttribute(
					targetOffset,
					living.getAttributeValue(Attributes.CAMERA_DISTANCE),
					Config.CLIENT.getCameraDistanceAttributeMode()
				);
			}
			
			targetOffset = applyModifiersAndMultipliers(
				targetOffset,
				defaultOffset,
				Config.CLIENT.getPassengerOffsetModifiers(),
				Config.CLIENT.getPassengerOffsetMultipliers()
			);
		}
		
		if(cameraEntity.isSprinting())
		{
			targetOffset = applyModifiersAndMultipliers(
				targetOffset,
				defaultOffset,
				Config.CLIENT.getSprintOffsetModifiers(),
				Config.CLIENT.getSprintOffsetMultipliers()
			);
		}
		
		if(this.instance.isAiming())
		{
			targetOffset = applyModifiersAndMultipliers(
				targetOffset,
				defaultOffset,
				Config.CLIENT.getAimingOffsetModifiers(),
				Config.CLIENT.getAimingOffsetMultipliers()
			);
		}
		
		if(cameraEntity instanceof LivingEntity living && living.isFallFlying())
		{
			targetOffset = applyModifiersAndMultipliers(
				targetOffset,
				defaultOffset,
				Config.CLIENT.getFallFlyingOffsetModifiers(),
				Config.CLIENT.getFallFlyingMultipliers()
			);
		}
		
		if(!cameraEntity.isSpectator())
		{
			if(cameraEntity instanceof LivingEntity living && living.onClimbable())
			{
				targetOffset = applyModifiersAndMultipliers(
					targetOffset,
					defaultOffset,
					Config.CLIENT.getClimbingOffsetModifiers(),
					Config.CLIENT.getClimbingMultipliers()
				);
			}
			
			if(camera.forwardVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
			{
				targetOffset = new Vec3(0, 0, targetOffset.z());
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				targetOffset = calcDynamicOffsets(camera, cameraEntity, level, targetOffset);
			}
		}
		
		double targetOffsetX = Config.CLIENT.isUnlimitedOffsetX() ? targetOffset.x() : Mth.clamp(targetOffset.x(), Config.CLIENT.getMinOffsetX(), Config.CLIENT.getMaxOffsetX());
		double targetOffsetY = Config.CLIENT.isUnlimitedOffsetY() ? targetOffset.y() : Mth.clamp(targetOffset.y(), Config.CLIENT.getMinOffsetY(), Config.CLIENT.getMaxOffsetY());
		double targetOffsetZ = Config.CLIENT.isUnlimitedOffsetZ() ? targetOffset.z() : Mth.clamp(targetOffset.z(), Config.CLIENT.getMinOffsetZ(), Config.CLIENT.getMaxOffsetZ());
		targetOffset = new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
		
		targetOffset = targetOffset.scale(EntityHelper.getMaxScale(cameraEntity));
		
		for(ITargetCameraOffsetCallback targetCameraOffsetCallback : targetCameraOffsetCallbacks)
		{
			targetOffset = targetCameraOffsetCallback.post(this.instance, targetOffset, defaultOffset);
		}
		
		this.targetOffset = targetOffset;
		Vec3 drag = this.calcCameraDrag(camera, cameraEntity, partialTick);
		Vec3 lerpedOffset = this.offsetO.lerp(this.offset, partialTick).add(drag);
		
		if(cameraEntity.isSpectator())
		{
			this.cameraDistance = lerpedOffset.length();
			this.renderOffset = lerpedOffset;
		}
		else
		{
			double targetCameraDistance = maxZoom(camera, level, lerpedOffset, partialTick);
			
			if(targetCameraDistance < this.maxCameraDistance)
			{
				this.maxCameraDistance = targetCameraDistance;
			}
			
			double lerpedMaxDistance = Mth.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance);
			this.cameraDistance = Math.min(targetCameraDistance, lerpedMaxDistance);
			this.renderOffset = lerpedOffset.normalize().scale(this.cameraDistance);
		}
		
		return this.renderOffset;
	}
	
	private static Vec3 applyModifiersAndMultipliers(Vec3 targetVec, Vec3 originalVec, Vec3 modifiers, Vec3 multipliers)
	{
		return targetVec.add(originalVec.multiply(multipliers).subtract(originalVec)).add(modifiers);
	}
	
	private static Vec3 applyCameraDistanceAttribute(Vec3 targetVec, double cameraDistance, CameraDistanceAttributeMode mode)
	{
		return switch(mode)
		{
			case RELATIVE -> targetVec.multiply(1.0D, 1.0D, cameraDistance / 4.0D);
			case ABSOLUTE -> new Vec3(targetVec.x, targetVec.y, cameraDistance);
			case IGNORE -> targetVec;
		};
	}
	
	private static Vec3 calcDynamicOffsets(Camera camera, Entity cameraEntity, BlockGetter level, Vec3 targetOffset)
	{
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
		
		for(double dz = 0; dz <= offsetZAbs; dz += 0.03125D)
		{
			double scale = dz / offsetZAbs;
			Vec3 startPos = cameraPosition.add(worldOffset.scale(scale));
			Vec3 endPos = cameraPosition.add(worldXYOffset).add(lookVector.scale(-dz));
			ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, cameraEntity);
			HitResult hitResult = level.clip(context);
			
			if(hitResult.getType() != HitResult.Type.MISS)
			{
				double distance = hitResult.getLocation().distanceTo(startPos);
				double newTargetX = Math.max(distance + offsetXAbs * scale - clearance, 0);
				
				if(newTargetX < targetX)
				{
					targetX = newTargetX;
				}
				
				double newTargetY = Math.max(distance + offsetYAbs * scale - clearance, 0);
				
				if(newTargetY < targetY)
				{
					targetY = newTargetY;
				}
			}
		}
		
		double targetXOffset = Math.signum(targetOffset.x()) * targetX;
		double targetYOffset = Math.signum(targetOffset.y()) * targetY;
		return new Vec3(targetXOffset, targetYOffset, targetOffset.z());
	}
	
	private static double maxZoom(Camera camera, BlockGetter level, Vec3 cameraOffset, float partialTick)
	{
		double distance = cameraOffset.length();
		Vec3 worldOffset = new Vec3(camera.upVector()).scale(cameraOffset.y())
			.add(new Vec3(camera.leftVector()).scale(cameraOffset.x()))
			.add(new Vec3(camera.forwardVector()).scale(-cameraOffset.z()));
		Vec3 eyePosition = camera.entity().getEyePosition(partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1);
			Vec3 fromOffset = offset.scale(Math.clamp(camera.entity().getBbWidth() / 2.0F / Mth.sqrt(2), 0.0F, 0.15F))
				.xRot(-camera.xRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.yRot() * Mth.DEG_TO_RAD);
			Vec3 from = eyePosition.add(fromOffset);
			Vec3 toOffset = offset.scale(0.15)
				.xRot(-camera.xRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.yRot() * Mth.DEG_TO_RAD);
			Vec3 to = eyePosition.add(toOffset).add(worldOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, camera.entity());
			HitResult hitResult = level.clip(context);
			
			if(hitResult.getType() != HitResult.Type.MISS)
			{
				double newDistance = hitResult.getLocation().distanceTo(eyePosition);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	private Vec3 calcCameraDrag(Camera cameraIn, Entity cameraEntity, float partialTick)
	{
		Vec3 deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.multiply(Config.CLIENT.getCameraDragMultipliers())
			.yRot(cameraIn.yRot() * Mth.DEG_TO_RAD)
			.xRot(cameraIn.xRot() * Mth.DEG_TO_RAD);
		return new Vec3(-deltaMovementLerped.x, -deltaMovementLerped.y, deltaMovementLerped.z);
	}
	
	public Vec2f calcSway(Entity cameraEntity, float partialTick)
	{
		Vec3 deltaMovement = EntityHelper.getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.yRot(this.getYRot() * Mth.DEG_TO_RAD)
			.xRot(this.getXRot() * Mth.DEG_TO_RAD);
		double maxVelocityX = Config.CLIENT.getCameraSwayXMaxVelocity() / 20;
		double maxVelocityZ = Config.CLIENT.getCameraSwayZMaxVelocity() / 20;
		double maxAngleX = Config.CLIENT.getCameraSwayXMaxAngle();
		double maxAngleZ = Config.CLIENT.getCameraSwayZMaxAngle();
		double swayX = Math.min(Math.abs(deltaMovementLerped.y), maxVelocityX) / maxVelocityX * maxAngleX * Math.signum(deltaMovementLerped.y);
		double swayZ = Math.min(Math.abs(deltaMovementLerped.x), maxVelocityZ) / maxVelocityZ * maxAngleZ * Math.signum(deltaMovementLerped.x);
		return new Vec2f((float) swayX, (float) swayZ);
	}
	
	public boolean turn(LocalPlayer player, double yRot, double xRot)
	{
		if(!this.instance.isShoulderSurfing())
		{
			return false;
		}
		
		if(yRot != 0.0F || xRot != 0.0F || EntityHelper.isPlayerSpectatingEntity())
		{
			this.followPlayerRotationsDelay = Config.CLIENT.getFollowPlayerRotationsDelay();
			this.followPlayerRotationsEaseIn = 1.0F;
			this.followPlayerRotationsEaseInO = 1.0F;
		}
		
		Vec2f cameraRot = CallbackHelper.fireCameraRotationSetupCallbackPre(player, yRot, xRot, this.rotation.y(), this.rotation.x()).getRotation();
		Vec2f scaledRot = new Vec2f((float) (xRot * 0.15F), (float) (yRot * 0.15F));
		
		if(this.instance.isFreeLooking())
		{
			this.rotationOffset = this.rotationOffset.add(scaledRot).clampX(-90F, 90F);
			this.rotationOffsetO = this.rotationOffset;
			return true;
		}
		
		cameraRot = cameraRot.add(scaledRot).clampX(-90F, 90F);
		
		if(player.isPassenger())
		{
			cameraRot = EntityHelper.applyPassengerRotationConstraints(player, cameraRot.x(), cameraRot.y(), this.rotation.x(), this.rotation.y());
		}
		
		this.rotation = CallbackHelper.fireCameraRotationSetupCallbackPost(player, yRot, xRot, cameraRot.y(), cameraRot.x()).getRotation();
		boolean isMoving = player.input.getMoveVector().x != 0.0F || player.input.getMoveVector().y != 0.0F || player.isFallFlying();
		
		if(this.instance.isCameraDecoupled())
		{
			if(!this.instance.isLookFollowingCrosshairTarget())
			{
				this.turnPlayerWithCamera(player, scaledRot, isMoving);
			}
			
			if(isMoving)
			{
				this.lastMovedYRot = player.getYRot();
			}
		}
		
		return this.instance.isCameraDecoupled();
	}
	
	private void turnPlayerWithCamera(LocalPlayer player, Vec2f scaledRot, boolean isMoving)
	{
		if(Config.CLIENT.shouldPlayerXRotFollowCamera() || Config.CLIENT.getFollowPlayerRotations())
		{
			player.setXRot(this.rotation.x());
			player.xRotO += Mth.degreesDifference(this.rotation.x(), this.rotation.x());
		}
		
		if((Config.CLIENT.shouldPlayerYRotFollowCamera() || Config.CLIENT.getFollowPlayerRotations()) && !isMoving)
		{
			float maxFollowAngle = (float) Config.CLIENT.getPlayerYRotFollowAngleLimit();
			float playerYRot = Mth.approachDegrees(this.lastMovedYRot, player.getYRot() + scaledRot.y(), maxFollowAngle);
			player.yRotO = player.getYRot();
			player.setYRot(playerYRot);
		}
	}
	
	private boolean shouldResetFollowPlayerRotationsDelay(Minecraft minecraft)
	{
		if(this.instance.isFreeLooking())
		{
			return true;
		}
		
		if(minecraft.player != null && minecraft.player.isScoping())
		{
			return true;
		}
		
		if(minecraft.screen != null)
		{
			return true;
		}
		
		return this.instance.isLookFollowingCrosshairTarget();
	}
	
	private static boolean shouldSyncCameraRotationsWithVehicleRotations(Minecraft minecraft, Entity entity)
	{
		return CallbackHelper.isRidingBoat(minecraft, entity);
	}
	
	public void resetState()
	{
		this.initialized = false;
	}
	
	@Override
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	@Override
	public Vec3 getOffset()
	{
		return this.offset;
	}
	
	@Override
	public Vec3 getRenderOffset()
	{
		return this.renderOffset;
	}
	
	@Override
	public Vec3 getTargetOffset()
	{
		return this.targetOffset;
	}
	
	@Override
	public float getXRot()
	{
		return this.rotation.x() + this.rotationOffset.x();
	}
	
	@Override
	public void setXRot(float xRot)
	{
		this.rotation = new Vec2f(0, this.rotation.y());
		this.rotationOffset = new Vec2f(0, this.rotationOffset.y());
		this.rotationOffsetO = new Vec2f(0, this.rotationOffsetO.y());
	}
	
	@Override
	public float getYRot()
	{
		return this.rotation.y() + this.rotationOffset.y();
	}
	
	@Override
	public void setYRot(float yRot)
	{
		this.rotation = new Vec2f(this.rotation.x(), 0);
		this.rotationOffset = new Vec2f(this.rotationOffset.x(), 0);
		this.rotationOffsetO = new Vec2f(this.rotationOffsetO.x(), 0);
	}
	
	public void setLastMovedYRot(float lastMovedYRot)
	{
		this.lastMovedYRot = lastMovedYRot;
	}
}
