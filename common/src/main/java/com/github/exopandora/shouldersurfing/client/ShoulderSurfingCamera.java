package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
		this.offsetO = this.offset;
		this.renderOffset = this.offset;
		this.targetOffset = this.offset;
		this.maxCameraDistance = this.offset.length();
		this.maxCameraDistanceO = this.maxCameraDistance;
		
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null)
		{
			this.xRot = cameraEntity.getXRot();
			this.yRot = cameraEntity.getYRot();
		}
		else
		{
			this.xRot = 0.0F;
			this.yRot = -180.0F;
		}
		
		this.xRotOffset = 0.0F;
		this.yRotOffset = 0.0F;
		this.xRotOffsetO = 0.0F;
		this.yRotOffsetO = 0.0F;
		this.initialized = true;
	}
	
	public Vec2f calcRotations(float partialTick)
	{
		float cameraXRotWithOffset = Mth.clamp(Mth.rotLerp(partialTick, this.xRotOffsetO, this.xRotOffset) + this.xRot, -90F, 90F);
		float cameraYRotWithOffset = Mth.rotLerp(partialTick, this.yRotOffsetO, this.yRotOffset) + this.yRot;
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
			Vec3 passengerOffsetMultipliers = Config.CLIENT.getPassengerOffsetMultipliers();
			Vec3 delta = defaultOffset.multiply(passengerOffsetMultipliers).subtract(defaultOffset);
			targetOffset = targetOffset.add(delta);
		}
		
		if(cameraEntity.isSprinting())
		{
			Vec3 sprintOffsetMultipliers = Config.CLIENT.getSprintOffsetMultipliers();
			Vec3 delta = defaultOffset.multiply(sprintOffsetMultipliers).subtract(defaultOffset);
			targetOffset = targetOffset.add(delta);
		}
		
		if(this.instance.isAiming())
		{
			Vec3 aimingOffsetMultipliers = Config.CLIENT.getAimingOffsetMultipliers();
			Vec3 delta = defaultOffset.multiply(aimingOffsetMultipliers).subtract(defaultOffset);
			targetOffset = targetOffset.add(delta);
		}

		if(cameraEntity instanceof LivingEntity living && living.isFallFlying())
		{
			Vec3 delta = Config.CLIENT.getFallFlyingOffsetModifiers();
			targetOffset = targetOffset.add(delta);
		}
		
		if(!cameraEntity.isSpectator())
		{
			if(shouldCenterCamera(cameraEntity))
			{
				targetOffset = new Vec3(0, targetOffset.y(), targetOffset.z());
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
		
		double targetOffsetX = Config.CLIENT.isUnlimitedOffsetX() ? targetOffset.x() : Math.min(targetOffset.x(), Config.CLIENT.getMaxOffsetX());
		double targetOffsetY = Config.CLIENT.isUnlimitedOffsetY() ? targetOffset.y() : Math.min(targetOffset.y(), Config.CLIENT.getMaxOffsetY());
		double targetOffsetZ = Config.CLIENT.isUnlimitedOffsetZ() ? targetOffset.z() : Math.min(targetOffset.z(), Config.CLIENT.getMaxOffsetZ());
		targetOffset = new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
		
		for(ITargetCameraOffsetCallback targetCameraOffsetCallback : targetCameraOffsetCallbacks)
		{
			targetOffset = targetCameraOffsetCallback.post(this.instance, targetOffset, defaultOffset);
		}
		
		this.targetOffset = targetOffset;
		Vec3 lerpedOffset = this.offsetO.lerp(this.offset, partialTick);
		
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
	
	private static boolean shouldCenterCamera(Entity entity)
	{
		return entity instanceof LivingEntity living && (Config.CLIENT.doCenterCameraWhenClimbing() && living.onClimbable() ||
			Config.CLIENT.doCenterCameraWhenFallFlying() && living.isFallFlying());
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
				.subtract(1, 1, 1)
				.scale(0.15)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 from = eyePosition.add(offset);
			Vec3 to = from.add(worldOffset);
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
	
	public boolean turn(Player player, double yRot, double xRot)
	{
		if(this.instance.isShoulderSurfing())
		{
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
			
			if(Config.CLIENT.isCameraDecoupled())
			{
				if(this.instance.isAiming() && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying())
				{
					player.setXRot(cameraXRot);
					player.setYRot(cameraYRot);
				}
				else if(Config.CLIENT.doSyncPlayerXRotWithInputs() && this.instance.isEntityRotationDecoupled(player, Minecraft.getInstance()))
				{
					player.setXRot(cameraXRot);
					player.xRotO += Mth.degreesDifference(this.xRot, cameraXRot);
				}
			}
			
			this.xRot = cameraXRot;
			this.yRot = cameraYRot;
			
			return Config.CLIENT.isCameraDecoupled();
		}
		
		return false;
	}
	
	private static Vec2f applyPassengerRotationConstraints(Player player, float cameraXRot, float cameraYRot, float cameraXRotO, float cameraYRotO)
	{
		Entity vehicle = player.getVehicle();
		float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		
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
}
