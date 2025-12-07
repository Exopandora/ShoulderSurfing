package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupContext;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback.CameraRotationSetupResult;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
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
	private float xRot;
	private float yRot;
	private float xRotOffset;
	private float yRotOffset;
	private float xRotOffsetO;
	private float yRotOffsetO;
	private float freeLookYRot;
	private float lastMovedYRot;
	private boolean initialized;
	
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
		this.xRotOffsetO = this.xRotOffset;
		this.yRotOffsetO = this.yRotOffset;
		this.offsetO = this.offset;
		this.offset = this.offsetO.lerp(this.targetOffset, cameraTransitionSpeedMultiplier);
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.maxCameraDistance = this.maxCameraDistance + (this.offset.length() - this.maxCameraDistance) * cameraTransitionSpeedMultiplier;
		
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		
		if(this.instance.isCameraDecoupled())
		{
			if(EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
			{
				this.xRot += living.getXRot() - living.xRotO;
				this.yRot += living.getYHeadRot() - living.yHeadRotO;
			}
		}
		else if(shouldSyncCameraRotationsWithVehicleRotations(minecraft, cameraEntity))
		{
			Entity vehicle = cameraEntity.getVehicle();
			
			if(vehicle != null)
			{
				this.yRot += vehicle.getYRot() - vehicle.yRotO;
			}
		}
		
		if(cameraEntity != null)
		{
			this.deltaMovementO = getDeltaMovementWithoutGravity(cameraEntity);
		}
		
		if(!this.instance.isFreeLooking())
		{
			this.freeLookYRot = this.yRot;
			this.xRotOffset *= 0.5F;
			this.yRotOffset *= 0.5F;
		}
	}
	
	private void init()
	{
		this.offset = new Vec3(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetZ());
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null)
		{
			this.offset = this.offset.scale(EntityHelper.getScale(cameraEntity));
			this.xRot = cameraEntity.getXRot();
			this.yRot = cameraEntity.getYRot();
			this.deltaMovementO = getDeltaMovementWithoutGravity(cameraEntity);
		}
		else
		{
			this.xRot = 0.0F;
			this.yRot = -180.0F;
			this.deltaMovementO = Vec3.ZERO;
		}
		
		this.offsetO = this.offset;
		this.renderOffset = this.offset;
		this.targetOffset = this.offset;
		this.maxCameraDistance = this.offset.length();
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.xRotOffset = 0.0F;
		this.yRotOffset = 0.0F;
		this.xRotOffsetO = 0.0F;
		this.yRotOffsetO = 0.0F;
		this.lastMovedYRot = this.yRot;
		this.initialized = true;
	}
	
	public Vec2f calcRotations(Entity cameraEntity, float partialTick)
	{
		if(!this.instance.isCameraDecoupled() && EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
		{
			return new Vec2f(living.getViewXRot(partialTick), living.getViewYRot(partialTick));
		}
		
		float cameraXRotWithOffset = Mth.clamp(Mth.rotLerp(partialTick, this.xRotOffsetO, this.xRotOffset) + this.xRot, -90F, 90F);
		float cameraYRotWithOffset = Mth.rotLerp(partialTick, this.yRotOffsetO, this.yRotOffset) + this.yRot;
		
		if(this.instance.isCameraDecoupled())
		{
			if(EntityHelper.isPlayerSpectatingEntity() && cameraEntity instanceof LivingEntity living)
			{
				cameraXRotWithOffset += (living.getXRot() - living.xRotO) * partialTick;
				cameraYRotWithOffset += (living.getYHeadRot() - living.yHeadRotO) * partialTick;
			}
		}
		else if(shouldSyncCameraRotationsWithVehicleRotations(Minecraft.getInstance(), cameraEntity))
		{
			Entity vehicle = cameraEntity.getVehicle();
			
			if(vehicle != null)
			{
				cameraYRotWithOffset += (vehicle.getYRot() - vehicle.yRotO) * partialTick;
			}
		}
		
		return new Vec2f(cameraXRotWithOffset, cameraYRotWithOffset);
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
		
		if(cameraEntity.isPassenger())
		{
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
			
			if(camera.getLookVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
			{
				targetOffset = new Vec3(0, 0, targetOffset.z());
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				targetOffset = calcDynamicOffsets(camera, cameraEntity, level, targetOffset);
			}
		}
		
		double targetOffsetX = Config.CLIENT.isUnlimitedOffsetX() ? targetOffset.x() : Math.clamp(targetOffset.x(), Config.CLIENT.getMinOffsetX(), Config.CLIENT.getMaxOffsetX());
		double targetOffsetY = Config.CLIENT.isUnlimitedOffsetY() ? targetOffset.y() : Math.clamp(targetOffset.y(), Config.CLIENT.getMinOffsetY(), Config.CLIENT.getMaxOffsetY());
		double targetOffsetZ = Config.CLIENT.isUnlimitedOffsetZ() ? targetOffset.z() : Math.clamp(targetOffset.z(), Config.CLIENT.getMinOffsetZ(), Config.CLIENT.getMaxOffsetZ());
		targetOffset = new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
		
		targetOffset = targetOffset.scale(getScale(cameraEntity));
		
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
	
	private static Vec3 calcDynamicOffsets(Camera camera, Entity cameraEntity, BlockGetter level, Vec3 targetOffset)
	{
		Vec3 lookVector = new Vec3(camera.getLookVector());
		Vec3 worldXYOffset = new Vec3(camera.getUpVector()).scale(targetOffset.y())
			.add(new Vec3(camera.getLeftVector()).scale(targetOffset.x()));
		Vec3 worldOffset = worldXYOffset.add(lookVector.scale(-targetOffset.z()));
		double offsetXAbs = Math.abs(targetOffset.x());
		double offsetYAbs = Math.abs(targetOffset.y());
		double offsetZAbs = Math.abs(targetOffset.z());
		double targetX = offsetXAbs;
		double targetY = offsetYAbs;
		double clearance = cameraEntity.getBbWidth() / 3.0D;
		Vec3 cameraPosition = camera.getPosition();
		
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
		Vec3 worldOffset = new Vec3(camera.getUpVector()).scale(cameraOffset.y())
			.add(new Vec3(camera.getLeftVector()).scale(cameraOffset.x()))
			.add(new Vec3(camera.getLookVector()).scale(-cameraOffset.z()));
		Vec3 eyePosition = camera.getEntity().getEyePosition(partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1);
			Vec3 fromOffset = offset.scale(Math.clamp(camera.getEntity().getBbWidth() / 2.0F / Mth.sqrt(2), 0.0F, 0.15F))
				.xRot(-camera.getXRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 from = eyePosition.add(fromOffset);
			Vec3 toOffset = offset.scale(0.15)
				.xRot(-camera.getXRot() * Mth.DEG_TO_RAD)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 to = eyePosition.add(toOffset).add(worldOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, camera.getEntity());
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
		Vec3 deltaMovement = getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.multiply(Config.CLIENT.getCameraDragMultipliers())
			.yRot(cameraIn.getYRot() * Mth.DEG_TO_RAD)
			.xRot(cameraIn.getXRot() * Mth.DEG_TO_RAD);
		return new Vec3(-deltaMovementLerped.x, -deltaMovementLerped.y, deltaMovementLerped.z);
	}
	
	public Vec2f calcSway(ShoulderSurfingCamera camera, Entity cameraEntity, float partialTick)
	{
		Vec3 deltaMovement = getDeltaMovementWithoutGravity(cameraEntity);
		Vec3 deltaMovementLerped = this.deltaMovementO.lerp(deltaMovement, partialTick)
			.yRot(camera.getYRot() * Mth.DEG_TO_RAD)
			.xRot(camera.getXRot() * Mth.DEG_TO_RAD);
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
		if(this.instance.isShoulderSurfing())
		{
			CameraRotationSetupResult preResult = fireCameraRotationSetupCallbackPre(player, yRot, xRot, this.yRot, this.xRot);
			this.xRot = preResult.getXRot();
			this.yRot = preResult.getYRot();
			
			float scaledXRot = (float) (xRot * 0.15F);
			float scaledYRot = (float) (yRot * 0.15F);
			
			if(this.instance.isFreeLooking())
			{
				this.xRotOffset = Mth.clamp(this.xRotOffset + scaledXRot, -90.0F, 90.0F);
				this.yRotOffset = Mth.wrapDegrees(this.yRotOffset + scaledYRot);
				this.xRotOffsetO = this.xRotOffset;
				this.yRotOffsetO = this.yRotOffset;
				return true;
			}
			
			float cameraXRot = Mth.clamp(this.xRot + scaledXRot, -90.0F, 90.0F);
			float cameraYRot = this.yRot + scaledYRot;
			
			if(player.isPassenger())
			{
				Vec2f constraintRotations = applyPassengerRotationConstraints(player, cameraXRot, cameraYRot, this.xRot, this.yRot);
				cameraXRot = constraintRotations.x();
				cameraYRot = constraintRotations.y();
			}
			
			if(this.instance.isCameraDecoupled())
			{
				boolean isMoving = player.input.getMoveVector().x != 0.0F || player.input.getMoveVector().y != 0.0F || player.isFallFlying();
				
				if(this.instance.shouldEntityFollowCamera(player))
				{
					player.setXRot(cameraXRot);
					player.setYRot(cameraYRot);
					player.xRotO += Mth.degreesDifference(this.xRot, cameraXRot);
					player.yRotO += Mth.degreesDifference(this.yRot, cameraYRot);
				}
				else if(!this.instance.shouldEntityAimAtTarget(player, Minecraft.getInstance()))
				{
					if(Config.CLIENT.shouldPlayerXRotFollowCamera())
					{
						player.setXRot(cameraXRot);
						player.xRotO += Mth.degreesDifference(this.xRot, cameraXRot);
					}
					
					if(Config.CLIENT.shouldPlayerYRotFollowCamera() && !isMoving)
					{
						float maxFollowAngle = (float) Config.CLIENT.getPlayerYRotFollowAngleLimit();
						float playerYRot = Mth.approachDegrees(this.lastMovedYRot, player.getYRot() + scaledYRot, maxFollowAngle);
						player.yRotO = player.getYRot();
						player.setYRot(playerYRot);
					}
				}
				
				if(isMoving)
				{
					this.lastMovedYRot = player.getYRot();
				}
			}
			
			CameraRotationSetupResult postResult = fireCameraRotationSetupCallbackPost(player, yRot, xRot, cameraYRot, cameraXRot);
			this.xRot = postResult.getXRot();
			this.yRot = postResult.getYRot();
			
			return this.instance.isCameraDecoupled();
		}
		
		return false;
	}
	
	private static float getScale(Entity cameraEntity)
	{
		Entity entity = cameraEntity;
		float scale = EntityHelper.getScale(entity);
		
		while(entity.getVehicle() != null)
		{
			entity = entity.getVehicle();
			scale = Math.max(scale, EntityHelper.getScale(entity));
		}
		
		return scale;
	}
	
	private static boolean shouldSyncCameraRotationsWithVehicleRotations(Minecraft minecraft, Entity entity)
	{
		if(!(entity instanceof LivingEntity))
		{
			return false;
		}
		
		Entity vehicle = entity.getVehicle();
		
		if(vehicle == null)
		{
			return false;
		}
		
		for(final IPlayerStateCallback callback : ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isRidingBoat(new IPlayerStateCallback.IsRidingBoatContext(minecraft, entity, vehicle));
			
			switch(result)
			{
				case TRUE -> { return true; }
				case FALSE -> { return false; }
				case PASS -> { /* Continue to next callback */ }
			}
		}
		
		return vehicle instanceof Boat;
	}
	
	private static CameraRotationSetupResult fireCameraRotationSetupCallbackPre(LocalPlayer player, double yRotDelta, double xRotDelta, float yRot, float xRot)
	{
		CameraRotationSetupContext context = new CameraRotationSetupContext(player, xRotDelta, yRotDelta);
		CameraRotationSetupResult result = new CameraRotationSetupResult(xRot, yRot);
		
		for(ICameraRotationSetupCallback callback : ShoulderSurfingRegistrar.getInstance().getSetupCameraRotationCallbacks())
		{
			callback.pre(context, result);
		}
		
		return result;
	}
	
	private static CameraRotationSetupResult fireCameraRotationSetupCallbackPost(LocalPlayer player, double yRotDelta, double xRotDelta, float yRot, float xRot)
	{
		CameraRotationSetupContext context = new CameraRotationSetupContext(player, xRotDelta, yRotDelta);
		CameraRotationSetupResult result = new CameraRotationSetupResult(xRot, yRot);
		
		for(ICameraRotationSetupCallback callback : ShoulderSurfingRegistrar.getInstance().getSetupCameraRotationCallbacks())
		{
			callback.post(context, result);
		}
		
		return result;
	}
	
	private static Vec2f applyPassengerRotationConstraints(Player player, float cameraXRot, float cameraYRot, float cameraXRotO, float cameraYRotO)
	{
		Entity vehicle = player.getVehicle();
		float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
		
		float playerXRot = player.getXRot();
		float playerYRot = player.getYRot();
		float playerXRotO = player.xRotO;
		float playerYRotO = player.yRotO;
		float playerYHeadRot = player.yHeadRot;
		float playerYHeadRotO = player.yHeadRotO;
		float playerYBodyRot = player.yBodyRot;
		float playerYBodyRotO = player.yBodyRotO;
		
		float vehicleXRot = vehicle.getXRot();
		float vehicleYRot = vehicle.getYRot();
		float vehicleXRotO = vehicle.xRotO;
		float vehicleYRotO = vehicle.yRotO;
		
		vehicle.setXRot(Mth.rotLerp(partialTick, vehicleXRotO, vehicleXRot));
		vehicle.setYRot(Mth.rotLerp(partialTick, vehicleYRotO, vehicleYRot));
		
		player.setXRot(cameraXRot);
		player.setYRot(cameraYRot);
		player.xRotO = cameraXRotO;
		player.yRotO = cameraYRotO;
		player.yHeadRot = cameraYRot;
		player.yHeadRotO = cameraYRotO;
		player.yBodyRot = cameraYRot;
		player.yBodyRotO = cameraYRotO;
		
		vehicle.onPassengerTurned(player);
		
		if(player.getXRot() != cameraXRot)
		{
			cameraXRot = player.getXRot();
		}
		
		if(player.getYRot() != cameraYRot)
		{
			cameraYRot = player.getYRot();
		}
		
		player.setXRot(playerXRot);
		player.setYRot(playerYRot);
		player.xRotO = playerXRotO;
		player.yRotO = playerYRotO;
		player.yHeadRot = playerYHeadRot;
		player.yHeadRotO = playerYHeadRotO;
		player.yBodyRot = playerYBodyRot;
		player.yBodyRotO = playerYBodyRotO;
		
		vehicle.setXRot(vehicleXRot);
		vehicle.setYRot(vehicleYRot);
		
		return new Vec2f(cameraXRot, cameraYRot);
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
		return this.xRot + this.xRotOffset;
	}
	
	@Override
	public void setXRot(float xRot)
	{
		this.xRot = xRot;
		this.xRotOffset = 0.0F;
		this.xRotOffsetO = 0.0F;
	}
	
	@Override
	public float getYRot()
	{
		return this.yRot + this.yRotOffset;
	}
	
	@Override
	public void setYRot(float yRot)
	{
		this.yRot = yRot;
		this.yRotOffset = 0.0F;
		this.yRotOffsetO = 0.0F;
	}
	
	public float getFreeLookYRot()
	{
		return this.freeLookYRot;
	}
	
	public void setLastMovedYRot(float lastMovedYRot)
	{
		this.lastMovedYRot = lastMovedYRot;
	}
	
	private static Vec3 getDeltaMovementWithoutGravity(Entity entity)
	{
		return entity.getDeltaMovement().add(0, entity.getGravity(), 0);
	}
}
