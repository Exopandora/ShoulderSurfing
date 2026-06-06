package com.github.exopandora.shouldersurfing.plugin.callbacks;

import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
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

public class TargetCameraOffsetCallback
{
	public static class CameraDistanceAttribute implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			if(context.cameraEntity() instanceof LivingEntity living)
			{
				return applyCameraDistanceAttribute(context.targetOffset(), living.getAttributeValue(Attributes.CAMERA_DISTANCE));
			}
			
			return context.targetOffset();
		}
	}
	
	public static class CameraDistanceAttributePassenger implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			if(context.cameraEntity().isPassenger() && context.cameraEntity().getVehicle() instanceof LivingEntity living)
			{
				return applyCameraDistanceAttribute(context.targetOffset(), living.getAttributeValue(Attributes.CAMERA_DISTANCE));
			}
			
			return context.targetOffset();
		}
	}
	
	private static abstract class AbstractModifiersAndMultipliers implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			if(this.shouldApply(context))
			{
				return context.targetOffset()
					.add(context.defaultOffset().multiply(this.getMultipliers()).subtract(context.defaultOffset()))
					.add(this.getModifiers());
			}
			
			return context.targetOffset();
		}
		
		protected abstract boolean shouldApply(Context context);
		
		protected abstract Vec3 getModifiers();
		
		protected abstract Vec3 getMultipliers();
	}
	
	public static class PassengerModifiersAndMultipliers extends AbstractModifiersAndMultipliers
	{
		@Override
		protected boolean shouldApply(Context context)
		{
			return context.cameraEntity().isPassenger();
		}
		
		@Override
		protected Vec3 getModifiers()
		{
			return Config.CLIENT.getPassengerOffsetModifiers();
		}
		
		@Override
		protected Vec3 getMultipliers()
		{
			return Config.CLIENT.getPassengerOffsetMultipliers();
		}
	}
	
	public static class SprintingModifiersAndMultipliers extends AbstractModifiersAndMultipliers
	{
		@Override
		protected boolean shouldApply(Context context)
		{
			return context.cameraEntity().isSprinting();
		}
		
		@Override
		protected Vec3 getModifiers()
		{
			return Config.CLIENT.getSprintOffsetModifiers();
		}
		
		@Override
		protected Vec3 getMultipliers()
		{
			return Config.CLIENT.getSprintOffsetMultipliers();
		}
	}
	
	public static class AimingModifiersAndMultipliers extends AbstractModifiersAndMultipliers
	{
		@Override
		protected boolean shouldApply(Context context)
		{
			return context.instance().isAiming();
		}
		
		@Override
		protected Vec3 getModifiers()
		{
			return Config.CLIENT.getAimingOffsetModifiers();
		}
		
		@Override
		protected Vec3 getMultipliers()
		{
			return Config.CLIENT.getAimingOffsetMultipliers();
		}
	}
	
	public static class FallFlyingModifiersAndMultipliers extends AbstractModifiersAndMultipliers
	{
		@Override
		protected boolean shouldApply(Context context)
		{
			return context.cameraEntity() instanceof LivingEntity living && living.isFallFlying();
		}
		
		@Override
		protected Vec3 getModifiers()
		{
			return Config.CLIENT.getFallFlyingOffsetModifiers();
		}
		
		@Override
		protected Vec3 getMultipliers()
		{
			return Config.CLIENT.getFallFlyingMultipliers();
		}
	}
	
	public static class ClimbingModifiersAndMultipliers extends AbstractModifiersAndMultipliers
	{
		@Override
		protected boolean shouldApply(Context context)
		{
			return !context.cameraEntity().isSpectator() && context.cameraEntity() instanceof LivingEntity living && living.onClimbable();
		}
		
		@Override
		protected Vec3 getModifiers()
		{
			return Config.CLIENT.getClimbingOffsetModifiers();
		}
		
		@Override
		protected Vec3 getMultipliers()
		{
			return Config.CLIENT.getClimbingMultipliers();
		}
	}
	
	public static class CenterWhenLookingDown implements ITargetCameraOffsetCallback
	{
		private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
		
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			if(!context.cameraEntity().isSpectator() && context.camera().forwardVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
			{
				return new Vec3(0, 0, context.targetOffset().z());
			}
			
			return context.targetOffset();
		}
	}
	
	public static class DynamicOffsets implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			if(!context.cameraEntity().isSpectator() && Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				return calcDynamicOffsets(context.camera(), context.cameraEntity(), context.level(), context.targetOffset());
			}
			
			return context.targetOffset();
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
	}
	
	public static class OffsetLimits implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			double targetOffsetX = Config.CLIENT.isUnlimitedOffsetX()
				? context.targetOffset().x()
				: Mth.clamp(context.targetOffset().x(), Config.CLIENT.getMinOffsetX(), Config.CLIENT.getMaxOffsetX());
			double targetOffsetY = Config.CLIENT.isUnlimitedOffsetY()
				? context.targetOffset().y()
				: Mth.clamp(context.targetOffset().y(), Config.CLIENT.getMinOffsetY(), Config.CLIENT.getMaxOffsetY());
			double targetOffsetZ = Config.CLIENT.isUnlimitedOffsetZ()
				? context.targetOffset().z()
				: Mth.clamp(context.targetOffset().z(), Config.CLIENT.getMinOffsetZ(), Config.CLIENT.getMaxOffsetZ());
			return new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
		}
	}
	
	public static class EntityScale implements ITargetCameraOffsetCallback
	{
		@Override
		public Vec3 getTargetOffset(Context context)
		{
			return context.targetOffset().scale(EntityHelper.getMaxScale(context.cameraEntity()));
		}
	}
	
	private static Vec3 applyCameraDistanceAttribute(Vec3 targetVec, double cameraDistance)
	{
		return switch(Config.CLIENT.getCameraDistanceAttributeMode())
		{
			case RELATIVE -> targetVec.multiply(1.0D, 1.0D, cameraDistance / 4.0D);
			case ABSOLUTE -> new Vec3(targetVec.x, targetVec.y, cameraDistance);
			case IGNORE -> targetVec;
		};
	}
}
