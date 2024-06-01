package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.accessors.ActiveRenderInfoAccessor;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.MathUtil;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;

public class ShoulderSurfingCamera implements IShoulderSurfingCamera
{
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
	private final ShoulderSurfingImpl instance;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private double offsetXO;
	private double offsetYO;
	private double offsetZO;
	private double offsetXTarget;
	private double offsetYTarget;
	private double offsetZTarget;
	private double cameraDistance;
	private double maxCameraDistance;
	private double maxCameraDistanceO;
	private Vector3d renderOffset;
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
		
		this.xRotOffsetO = this.xRotOffset;
		this.yRotOffsetO = this.yRotOffset;
		
		this.offsetXO = this.offsetX;
		this.offsetYO = this.offsetY;
		this.offsetZO = this.offsetZ;
		
		double cameraTransitionSpeedMultiplier = Config.CLIENT.getCameraTransitionSpeedMultiplier();
		
		this.offsetX = this.offsetXO + (this.offsetXTarget - this.offsetXO) * cameraTransitionSpeedMultiplier;
		this.offsetY = this.offsetYO + (this.offsetYTarget - this.offsetYO) * cameraTransitionSpeedMultiplier;
		this.offsetZ = this.offsetZO + (this.offsetZTarget - this.offsetZO) * cameraTransitionSpeedMultiplier;
		
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.maxCameraDistance = this.maxCameraDistance + (this.getOffset().length() - this.maxCameraDistance) * cameraTransitionSpeedMultiplier;
		
		if(!this.instance.isFreeLooking())
		{
			this.freeLookYRot = this.yRot;
			this.xRotOffset *= 0.5F;
			this.yRotOffset *= 0.5F;
		}
	}
	
	private void init()
	{
		this.offsetX = Config.CLIENT.getOffsetX();
		this.offsetY = Config.CLIENT.getOffsetY();
		this.offsetZ = Config.CLIENT.getOffsetZ();
		this.offsetXO = this.offsetX;
		this.offsetYO = this.offsetY;
		this.offsetZO = this.offsetZ;
		this.offsetXTarget = this.offsetX;
		this.offsetYTarget = this.offsetY;
		this.offsetZTarget = this.offsetZ;
		this.renderOffset = new Vector3d(this.offsetX, this.offsetY, this.offsetZ);
		this.maxCameraDistance = this.renderOffset.length();
		this.maxCameraDistanceO = this.maxCameraDistance;
		
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null)
		{
			this.xRot = cameraEntity.xRot;
			this.yRot = cameraEntity.yRot;
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
		float cameraXRotWithOffset = MathHelper.clamp(MathHelper.rotLerp(partialTick, this.xRotOffsetO, this.xRotOffset) + this.xRot, -90F, 90F);
		float cameraYRotWithOffset = MathHelper.rotLerp(partialTick, this.yRotOffsetO, this.yRotOffset) + this.yRot;
		return new Vec2f(cameraXRotWithOffset, cameraYRotWithOffset);
	}
	
	public Vector3d calcOffset(ActiveRenderInfo camera, IBlockReader level, float partialTick, Entity cameraEntity, double maxZoom)
	{
		double targetXOffset = Config.CLIENT.getOffsetX();
		double targetYOffset = Config.CLIENT.getOffsetY();
		double targetZOffset = Config.CLIENT.getOffsetZ();
		
		if(cameraEntity.isPassenger())
		{
			targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getPassengerOffsetXMultiplier() - 1);
			targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getPassengerOffsetYMultiplier() - 1);
			targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getPassengerOffsetZMultiplier() - 1);
		}
		
		if(cameraEntity.isSprinting())
		{
			targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getSprintOffsetXMultiplier() - 1);
			targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getSprintOffsetYMultiplier() - 1);
			targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getSprintOffsetZMultiplier() - 1);
		}
		
		if(this.instance.isAiming())
		{
			targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getAimingOffsetXMultiplier() - 1);
			targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getAimingOffsetYMultiplier() - 1);
			targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getAimingOffsetZMultiplier() - 1);
		}
		
		if(!cameraEntity.isSpectator())
		{
			if(shouldCenterCamera(cameraEntity))
			{
				targetXOffset = 0;
			}
			
			if(angle(camera.getLookVector(), VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * MathUtil.DEG_TO_RAD)
			{
				targetXOffset = 0;
				targetYOffset = 0;
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				Vector3d targetOffsets = calcDynamicOffsets(camera, cameraEntity, level, targetXOffset, targetYOffset, targetZOffset);
				targetXOffset = targetOffsets.x();
				targetYOffset = targetOffsets.y();
				targetZOffset = targetOffsets.z();
			}
		}
		
		this.offsetXTarget = targetXOffset;
		this.offsetYTarget = targetYOffset;
		this.offsetZTarget = targetZOffset;
		
		double offsetX = MathHelper.lerp(partialTick, this.offsetXO, this.offsetX);
		double offsetY = MathHelper.lerp(partialTick, this.offsetYO, this.offsetY);
		double offsetZ = MathHelper.lerp(partialTick, this.offsetZO, this.offsetZ);
		
		Vector3d offset = new Vector3d(offsetX, offsetY, offsetZ);
		
		if(cameraEntity.isSpectator())
		{
			this.cameraDistance = offset.length();
			this.renderOffset = offset;
		}
		else
		{
			double targetCameraDistance = maxZoom(camera, level, offset, maxZoom, partialTick);
			
			if(targetCameraDistance < this.maxCameraDistance)
			{
				this.maxCameraDistance = targetCameraDistance;
			}
			
			double lerpedMaxDistance = MathHelper.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance);
			this.cameraDistance = Math.min(targetCameraDistance, lerpedMaxDistance);
			this.renderOffset = offset.normalize().scale(this.cameraDistance);
		}
		
		return new Vector3d(-this.renderOffset.z(), this.renderOffset.y(), this.renderOffset.x());
	}
	
	private static boolean shouldCenterCamera(Entity entity)
	{
		return entity instanceof LivingEntity && (Config.CLIENT.doCenterCameraWhenClimbing() && ((LivingEntity) entity).onClimbable() ||
			Config.CLIENT.doCenterCameraWhenFallFlying() && ((LivingEntity) entity).isFallFlying());
	}
	
	private static Vector3d calcDynamicOffsets(ActiveRenderInfo camera, Entity cameraEntity, IBlockReader level, double targetXOffset, double targetYOffset, double targetZOffset)
	{
		Vector3d lookVector = new Vector3d(camera.getLookVector());
		Vector3d worldXYOffset = new Vector3d(camera.getUpVector()).scale(targetYOffset)
			.add(new Vector3d(((ActiveRenderInfoAccessor) camera).getLeft()).scale(targetXOffset));
		Vector3d worldOffset = worldXYOffset.add(lookVector.scale(-targetZOffset));
		double offsetXAbs = Math.abs(targetXOffset);
		double offsetYAbs = Math.abs(targetYOffset);
		double offsetZAbs = Math.abs(targetZOffset);
		double targetX = offsetXAbs;
		double targetY = offsetYAbs;
		double clearance = cameraEntity.getBbWidth() / 3.0D;
		Vector3d cameraPosition = camera.getPosition();
		
		for(double dz = 0; dz <= offsetZAbs; dz += 0.03125D)
		{
			double scale = dz / offsetZAbs;
			Vector3d startPos = cameraPosition.add(worldOffset.scale(scale));
			Vector3d endPos = cameraPosition.add(worldXYOffset).add(lookVector.scale(-dz));
			RayTraceContext context = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, cameraEntity);
			RayTraceResult hitResult = level.clip(context);
			
			if(hitResult.getType() != RayTraceResult.Type.MISS)
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
		
		targetXOffset = Math.signum(Config.CLIENT.getOffsetX()) * targetX;
		targetYOffset = Math.signum(Config.CLIENT.getOffsetY()) * targetY;
		
		return new Vector3d(targetXOffset, targetYOffset, targetZOffset);
	}
	
	private static double maxZoom(ActiveRenderInfo camera, IBlockReader level, Vector3d cameraOffset, double distance, float partialTick)
	{
		Vector3d worldOffset = new Vector3d(camera.getUpVector()).scale(cameraOffset.y())
			.add(new Vector3d(((ActiveRenderInfoAccessor) camera).getLeft()).scale(cameraOffset.x()))
			.add(new Vector3d(camera.getLookVector()).scale(-cameraOffset.z()))
			.normalize()
			.scale(distance);
		Vector3d eyePosition = camera.getEntity().getEyePosition(partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.15)
				.yRot(-camera.getYRot() * MathUtil.DEG_TO_RAD);
			Vector3d from = eyePosition.add(offset);
			Vector3d to = from.add(worldOffset);
			RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, camera.getEntity());
			RayTraceResult hitResult = level.clip(context);
			
			if(hitResult.getType() != RayTraceResult.Type.MISS)
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
	
	public boolean turn(PlayerEntity player, double yRot, double xRot)
	{
		if(this.instance.isShoulderSurfing())
		{
			float scaledXRot = (float) (xRot * 0.15F);
			float scaledYRot = (float) (yRot * 0.15F);
			
			if(this.instance.isFreeLooking())
			{
				this.xRotOffset = MathHelper.clamp(this.xRotOffset + scaledXRot, -90.0F, 90.0F);
				this.yRotOffset = MathHelper.wrapDegrees(this.yRotOffset + scaledYRot);
				this.xRotOffsetO = this.xRotOffset;
				this.yRotOffsetO = this.yRotOffset;
				return true;
			}
			
			float cameraXRot = MathHelper.clamp(this.xRot + scaledXRot, -90.0F, 90.0F);
			float cameraYRot = this.yRot + scaledYRot;
			
			if(player.isPassenger())
			{
				Vec2f constraintRotations = applyPassengerRotationConstraints(player, cameraXRot, cameraYRot, this.xRot, this.yRot);
				cameraXRot = constraintRotations.x();
				cameraYRot = constraintRotations.y();
			}
			
			if(Config.CLIENT.isCameraDecoupled() && (this.instance.isAiming() && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying()))
			{
				player.xRot = cameraXRot;
				player.yRot = cameraYRot;
			}
			
			this.xRot = cameraXRot;
			this.yRot = cameraYRot;
			
			return Config.CLIENT.isCameraDecoupled();
		}
		
		return false;
	}
	
	private static Vec2f applyPassengerRotationConstraints(PlayerEntity player, float cameraXRot, float cameraYRot, float cameraXRotO, float cameraYRotO)
	{
		Entity vehicle = player.getVehicle();
		float partialTick = Minecraft.getInstance().getFrameTime();
		
		float playerXRot = player.xRot;
		float playerYRot = player.yRot;
		float playerXRotO = player.xRotO;
		float playerYRotO = player.yRotO;
		float playerYHeadRot = player.yHeadRot;
		float playerYHeadRotO = player.yHeadRotO;
		float playerYBodyRot = player.yBodyRot;
		float playerYBodyRotO = player.yBodyRotO;
		
		float vehicleXRot = vehicle.xRot;
		float vehicleYRot = vehicle.yRot;
		float vehicleXRotO = vehicle.xRotO;
		float vehicleYRotO = vehicle.yRotO;
		
		vehicle.xRot = MathHelper.rotLerp(partialTick, vehicleXRotO, vehicleXRot);
		vehicle.yRot = MathHelper.rotLerp(partialTick, vehicleYRotO, vehicleYRot);
		
		player.xRot = cameraXRot;
		player.yRot = cameraYRot;
		player.xRotO = cameraXRotO;
		player.yRotO = cameraYRotO;
		player.yHeadRot = cameraYRot;
		player.yHeadRotO = cameraYRotO;
		player.yBodyRot = cameraYRot;
		player.yBodyRotO = cameraYRotO;
		
		vehicle.onPassengerTurned(player);
		
		if(player.xRot != cameraXRot)
		{
			cameraXRot = player.xRot;
		}
		
		if(player.yRot != cameraYRot)
		{
			cameraYRot = player.yRot;
		}
		
		player.xRot = playerXRot;
		player.yRot = playerYRot;
		player.xRotO = playerXRotO;
		player.yRotO = playerYRotO;
		player.yHeadRot = playerYHeadRot;
		player.yHeadRotO = playerYHeadRotO;
		player.yBodyRot = playerYBodyRot;
		player.yBodyRotO = playerYBodyRotO;
		
		vehicle.xRot = vehicleXRot;
		vehicle.yRot = vehicleYRot;
		
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
	public Vector3d getOffset()
	{
		return new Vector3d(this.offsetX, this.offsetY, this.offsetZ);
	}
	
	@Override
	public Vector3d getRenderOffset()
	{
		return this.renderOffset;
	}
	
	@Override
	public Vector3d getTargetOffset()
	{
		return new Vector3d(this.offsetXTarget, this.offsetYTarget, this.offsetZTarget);
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
	
	public static double length(Vector3f vec)
	{
		return MathHelper.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
	
	public static double angle(Vector3f a, Vector3f b)
	{
		return Math.acos(a.dot(b) / (length(a) * length(b)));
	}
}
