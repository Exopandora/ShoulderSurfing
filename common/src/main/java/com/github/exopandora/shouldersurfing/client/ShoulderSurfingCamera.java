package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.accessors.ActiveRenderInfoAccessor;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.MathUtil;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
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

import java.util.List;

public class ShoulderSurfingCamera implements IShoulderSurfingCamera
{
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
	private final ShoulderSurfingImpl instance;
	private Vector3d offset;
	private Vector3d offsetO;
	private Vector3d renderOffset;
	private Vector3d targetOffset;
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
		this.offset = lerp(this.offsetO, this.targetOffset, cameraTransitionSpeedMultiplier);
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
		this.offset = new Vector3d(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetZ());
		this.offsetO = this.offset;
		this.renderOffset = this.offset;
		this.targetOffset = this.offset;
		this.maxCameraDistance = this.offset.length();
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
	
	public Vector3d calcOffset(ActiveRenderInfo camera, IBlockReader level, float partialTick, Entity cameraEntity)
	{
		Vector3d defaultOffset = new Vector3d(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetZ());
		Vector3d targetOffset = defaultOffset;
		List<ITargetCameraOffsetCallback> targetCameraOffsetCallbacks = ShoulderSurfingRegistrar.getInstance().getTargetCameraOffsetCallbacks();
		
		for(ITargetCameraOffsetCallback targetCameraOffsetCallback : targetCameraOffsetCallbacks)
		{
			targetOffset = targetCameraOffsetCallback.pre(this.instance, targetOffset, defaultOffset);
		}
		
		if(cameraEntity.isPassenger())
		{
			targetOffset = targetOffset.add(defaultOffset.scale(Config.CLIENT.getPassengerOffsetXMultiplier() - 1));
		}
		
		if(cameraEntity.isSprinting())
		{
			targetOffset = targetOffset.add(defaultOffset.scale(Config.CLIENT.getSprintOffsetXMultiplier() - 1));
		}
		
		if(this.instance.isAiming())
		{
			targetOffset = targetOffset.add(defaultOffset.scale(Config.CLIENT.getAimingOffsetXMultiplier() - 1));
		}
		
		if(!cameraEntity.isSpectator())
		{
			if(shouldCenterCamera(cameraEntity))
			{
				targetOffset = new Vector3d(0, targetOffset.y(), targetOffset.z());
			}
			
			if(angle(camera.getLookVector(), VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * MathUtil.DEG_TO_RAD)
			{
				targetOffset = new Vector3d(0, 0, targetOffset.z());
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				targetOffset = calcDynamicOffsets(camera, cameraEntity, level, targetOffset);
			}
		}
		
		for(ITargetCameraOffsetCallback targetCameraOffsetCallback : targetCameraOffsetCallbacks)
		{
			targetOffset = targetCameraOffsetCallback.post(this.instance, targetOffset, defaultOffset);
		}
		
		this.targetOffset = targetOffset;
		Vector3d lerpedOffset = lerp(this.offsetO, this.offset, partialTick);
		
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
			
			double lerpedMaxDistance = MathHelper.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance);
			this.cameraDistance = Math.min(targetCameraDistance, lerpedMaxDistance);
			this.renderOffset = lerpedOffset.normalize().scale(this.cameraDistance);
		}
		
		return new Vector3d(-this.renderOffset.z(), this.renderOffset.y(), this.renderOffset.x());
	}
	
	private static boolean shouldCenterCamera(Entity entity)
	{
		return entity instanceof LivingEntity && (Config.CLIENT.doCenterCameraWhenClimbing() && ((LivingEntity) entity).onClimbable() ||
			Config.CLIENT.doCenterCameraWhenFallFlying() && ((LivingEntity) entity).isFallFlying());
	}
	
	private static Vector3d calcDynamicOffsets(ActiveRenderInfo camera, Entity cameraEntity, IBlockReader level, Vector3d targetOffset)
	{
		Vector3d lookVector = new Vector3d(camera.getLookVector());
		Vector3d worldXYOffset = new Vector3d(camera.getUpVector()).scale(targetOffset.y())
			.add(new Vector3d(((ActiveRenderInfoAccessor) camera).getLeft()).scale(targetOffset.x()));
		Vector3d worldOffset = worldXYOffset.add(lookVector.scale(-targetOffset.z()));
		double offsetXAbs = Math.abs(targetOffset.x());
		double offsetYAbs = Math.abs(targetOffset.y());
		double offsetZAbs = Math.abs(targetOffset.z());
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
		
		double targetXOffset = Math.signum(Config.CLIENT.getOffsetX()) * targetX;
		double targetYOffset = Math.signum(Config.CLIENT.getOffsetY()) * targetY;
		return new Vector3d(targetXOffset, targetYOffset, targetOffset.z());
	}
	
	private static double maxZoom(ActiveRenderInfo camera, IBlockReader level, Vector3d cameraOffset, float partialTick)
	{
		double distance = cameraOffset.length();
		Vector3d worldOffset = new Vector3d(camera.getUpVector()).scale(cameraOffset.y())
			.add(new Vector3d(((ActiveRenderInfoAccessor) camera).getLeft()).scale(cameraOffset.x()))
			.add(new Vector3d(camera.getLookVector()).scale(-cameraOffset.z()));
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
			
			if(Config.CLIENT.isCameraDecoupled())
			{
				if(this.instance.isAiming() && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying())
				{
					player.xRot = cameraXRot;
					player.yRot = cameraYRot;
				}
				else if(Config.CLIENT.doSyncPlayerXRotWithInputs() && this.instance.isEntityRotationDecoupled(player, Minecraft.getInstance()))
				{
					player.xRot = cameraXRot;
					player.xRotO += MathHelper.degreesDifference(this.xRot, cameraXRot);
				}
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
		return this.offset;
	}
	
	@Override
	public Vector3d getRenderOffset()
	{
		return this.renderOffset;
	}
	
	@Override
	public Vector3d getTargetOffset()
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
	
	public static double length(Vector3f vec)
	{
		return MathHelper.sqrt(vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z());
	}
	
	public static double angle(Vector3f a, Vector3f b)
	{
		return Math.acos(a.dot(b) / (length(a) * length(b)));
	}
	
	public static Vector3d lerp(Vector3d a, Vector3d b, double d)
	{
		return new Vector3d(MathHelper.lerp(d, a.x(), b.x()), MathHelper.lerp(d, a.y(), b.y()), MathHelper.lerp(d, a.z(), b.z()));
	}
}
