package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixins.CameraAccessor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Locale;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private static final Vector3f VECTOR_NEGATIVE_Y = new Vector3f(0, -1, 0);
	private double cameraDistance;
	private double targetCameraDistance = ShoulderInstance.getInstance().getOffset().length();
	private double maxCameraDistance = this.targetCameraDistance;
	private double maxCameraDistanceO = this.targetCameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	private double cameraOffsetX;
	private double cameraOffsetY;
	private double cameraOffsetZ;
	private float cameraEntityAlpha = 1.0F;
	private float cameraXRot = 0F;
	private float cameraYRot = 0F;
	private float cameraXRotOffset = 0F;
	private float cameraYRotOffset = 0F;
	private float cameraXRotOffsetO = 0F;
	private float cameraYRotOffsetO = 0F;
	
	public void tick()
	{
		this.cameraXRotOffsetO = this.cameraXRotOffset;
		this.cameraYRotOffsetO = this.cameraYRotOffset;
		this.maxCameraDistanceO = this.maxCameraDistance;
		this.maxCameraDistance = Math.min(this.targetCameraDistance, this.maxCameraDistance + (ShoulderInstance.getInstance().getOffset().length() - this.maxCameraDistance) * Config.CLIENT.getCameraTransitionSpeedMultiplier());
		
		if(!ShoulderInstance.getInstance().isFreeLooking())
		{
			this.cameraXRotOffset *= 0.5F;
			this.cameraYRotOffset *= 0.5F;
		}
	}
	
	public void offsetCrosshair(PoseStack poseStack, Window window, float partialTicks)
	{
		if(this.projected != null)
		{
			Vec2f scaledDimensions = new Vec2f(window.getGuiScaledWidth(), window.getGuiScaledHeight());
			Vec2f dimensions = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f scale = scaledDimensions.divide(dimensions);
			Vec2f center = dimensions.divide(2); // In actual monitor pixels
			Vec2f projectedOffset = this.projected.subtract(center).scale(scale);
			Vec2f interpolated = projectedOffset.subtract(this.lastTranslation).scale(partialTicks);
			this.translation = this.lastTranslation.add(interpolated);
		}
		
		if(ShoulderInstance.getInstance().isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()))
		{
			poseStack.pushPose();
			poseStack.last().pose().translate(this.translation.x(), -this.translation.y(), 0F);
			this.lastTranslation = this.translation;
		}
		else
		{
			this.lastTranslation = Vec2f.ZERO;
		}
	}
	
	public void clearCrosshairOffset(PoseStack poseStack)
	{
		if(ShoulderInstance.getInstance().isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()) && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(Camera camera, Level level, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null && !(camera.getEntity() instanceof LivingEntity cameraEntity && cameraEntity.isSleeping()))
		{
			CameraAccessor accessor = ((CameraAccessor) camera);
			float cameraXRotWithOffset = Mth.clamp(Mth.rotLerp(partialTick, this.cameraXRotOffsetO, this.cameraXRotOffset) + this.cameraXRot, -90F, 90F);
			float cameraYRotWithOffset = Mth.rotLerp(partialTick, this.cameraYRotOffsetO, this.cameraYRotOffset) + this.cameraYRot;
			accessor.invokeSetRotation(cameraYRotWithOffset, cameraXRotWithOffset);
			
			double targetXOffset = Config.CLIENT.getOffsetX();
			double targetYOffset = Config.CLIENT.getOffsetY();
			double targetZOffset = Config.CLIENT.getOffsetZ();
			
			if(camera.getEntity().isPassenger())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getPassengerOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getPassengerOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getPassengerOffsetZMultiplier() - 1);
			}
			
			if(camera.getEntity().isSprinting())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getSprintOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getSprintOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getSprintOffsetZMultiplier() - 1);
			}
			
			if(!camera.getEntity().isSpectator())
			{
				if(shouldCenterCamera(camera.getEntity()))
				{
					targetXOffset = 0;
				}
				
				if(camera.getLookVector().angle(VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * Mth.DEG_TO_RAD)
				{
					targetXOffset = 0;
					targetYOffset = 0;
				}
				
				if(Config.CLIENT.doDynamicallyAdjustOffsets())
				{
					Vec3 localCameraOffset = new Vec3(targetXOffset, targetYOffset, targetZOffset);
					Vec3 worldCameraOffset = new Vec3(camera.getUpVector()).scale(targetYOffset)
						.add(new Vec3(camera.getLeftVector()).scale(targetXOffset))
						.add(new Vec3(camera.getLookVector()).scale(-targetZOffset))
						.normalize()
						.scale(localCameraOffset.length());
					Vec3 worldXYOffset = ShoulderHelper.calcRayTraceStartOffset(camera, worldCameraOffset);
					Vec3 eyePosition = camera.getEntity().getEyePosition(partialTick);
					double absOffsetX = Math.abs(targetXOffset);
					double absOffsetY = Math.abs(targetYOffset);
					double absOffsetZ = Math.abs(targetZOffset);
					double targetX = absOffsetX;
					double targetY = absOffsetY;
					double clearance = camera.getEntity().getBbWidth() / 3.0D;
					
					for(double dz = 0; dz <= absOffsetZ; dz += 0.03125D)
					{
						double scale = dz / absOffsetZ;
						Vec3 from = eyePosition.add(worldCameraOffset.scale(scale));
						Vec3 to = eyePosition.add(worldXYOffset).add(new Vec3(camera.getLookVector()).scale(-dz));
						ClipContext context = new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, camera.getEntity());
						HitResult hitResult = level.clip(context);
						
						if(hitResult.getType() != HitResult.Type.MISS)
						{
							double distance = hitResult.getLocation().distanceTo(from);
							double newTargetX = Math.max(distance + absOffsetX * scale - clearance, 0);
							
							if(newTargetX < targetX)
							{
								targetX = newTargetX;
							}
							
							double newTargetY = Math.max(distance + absOffsetY * scale - clearance, 0);
							
							if(newTargetY < targetY)
							{
								targetY = newTargetY;
							}
						}
					}
					
					targetXOffset = Math.signum(Config.CLIENT.getOffsetX()) * targetX;
					targetYOffset = Math.signum(Config.CLIENT.getOffsetY()) * targetY;
				}
			}
			
			ShoulderInstance instance = ShoulderInstance.getInstance();
			instance.setTargetOffsetX(targetXOffset);
			instance.setTargetOffsetY(targetYOffset);
			instance.setTargetOffsetZ(targetZOffset);
			
			double x = Mth.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = Mth.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = Mth.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = Mth.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = Mth.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vec3 offset = new Vec3(offsetX, offsetY, offsetZ);
			
			if(!camera.getEntity().isSpectator())
			{
				this.targetCameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()), partialTick);
				
				if(this.targetCameraDistance < this.maxCameraDistance)
				{
					this.maxCameraDistance = this.targetCameraDistance;
					this.cameraDistance = this.targetCameraDistance;
				}
				else
				{
					this.cameraDistance = Math.min(this.targetCameraDistance, Mth.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance));
				}
				
				Vec3 scaled = offset.normalize().scale(this.cameraDistance);
				this.cameraOffsetX = scaled.x;
				this.cameraOffsetY = scaled.y;
				this.cameraOffsetZ = scaled.z;
				accessor.invokeMove(-scaled.z, scaled.y, scaled.x);
			}
			else
			{
				this.cameraDistance = offset.length();
				this.cameraOffsetX = offset.x;
				this.cameraOffsetY = offset.y;
				this.cameraOffsetZ = offset.z;
				accessor.invokeMove(-offset.z, offset.y, offset.x);
			}
		}
	}
	
	private static boolean shouldCenterCamera(Entity entity) {
		return entity instanceof LivingEntity living && (Config.CLIENT.doCenterCameraWhenClimbing() && living.onClimbable() ||
			Config.CLIENT.doCenterCameraWhenFallFlying() && living.isFallFlying());
	}
	
	private double calcCameraDistance(Camera camera, Level level, double distance, float partialTick)
	{
		ShoulderInstance instance = ShoulderInstance.getInstance();
		double offsetX = Mth.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
		double offsetY = Mth.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
		double offsetZ = Mth.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
		Vec3 cameraOffset = new Vec3(camera.getUpVector()).scale(offsetY)
			.add(new Vec3(camera.getLeftVector()).scale(offsetX))
			.add(new Vec3(camera.getLookVector()).scale(-offsetZ))
			.normalize()
			.scale(distance);
		Vec3 eyePosition = camera.getEntity().getEyePosition(partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.15)
				.yRot(-camera.getYRot() * Mth.DEG_TO_RAD);
			Vec3 from = eyePosition.add(offset);
			Vec3 to = from.add(cameraOffset);
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
	
	public void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && Minecraft.getInstance().player != null)
		{
			HitResult hitResult = ShoulderRayTracer.traceBlocksAndEntities(camera, Minecraft.getInstance().player, this.getPlayerReach(), ClipContext.Fluid.NONE, partialTick, true, false);
			Vec3 position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = ShoulderHelper.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	private boolean shouldSkipCameraEntityRendering(Entity cameraEntity)
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() && !cameraEntity.isSpectator() &&
			(this.cameraDistance < cameraEntity.getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier() ||
				this.cameraXRot < Config.CLIENT.getHidePlayerWhenLookingUpAngle() - 90 ||
				cameraEntity instanceof Player player && player.isScoping());
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick)
	{
		if(this.shouldSkipCameraEntityRendering(entity))
		{
			return true;
		}
		
		if(this.shouldRenderCameraEntityTransparent(entity))
		{
			float xAlpha = (float) Mth.clamp(Math.abs(this.cameraOffsetX) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			
			if(this.cameraOffsetY > 0)
			{
				yAlpha = (float) Mth.clamp(this.cameraOffsetY / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			}
			else if(this.cameraOffsetY < 0)
			{
				yAlpha = (float) Mth.clamp(-this.cameraOffsetY / -entity.getEyeHeight(), 0, 1.0F);
			}
			
			this.cameraEntityAlpha = Mth.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), 0.15F, 1.0F);
		}
		
		return false;
	}
	
	public void postRenderCameraEntity(Entity entity, float partialTick)
	{
		this.cameraEntityAlpha = 1.0F;
	}
	
	private boolean shouldRenderCameraEntityTransparent(Entity entity)
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() && Config.CLIENT.isPlayerTransparencyEnabled() &&
			!entity.isSpectator() && (Math.abs(this.cameraOffsetX) < (entity.getBbWidth() / 2.0D) &&
				(this.cameraOffsetY >= 0 && this.cameraOffsetY < entity.getBbHeight() - entity.getEyeHeight() ||
					this.cameraOffsetY <= 0 && -this.cameraOffsetY < entity.getEyeHeight()));
	}
	
	public boolean turn(Player player, double yRot, double xRot)
	{
		ShoulderInstance instance = ShoulderInstance.getInstance();
		
		if(instance.doShoulderSurfing())
		{
			float scaledXRot = (float) (xRot * 0.15F);
			float scaledYRot = (float) (yRot * 0.15F);
			
			if(instance.isFreeLooking())
			{
				this.cameraXRotOffset = Mth.clamp(this.cameraXRotOffset + scaledXRot, -90.0F, 90.0F);
				this.cameraYRotOffset = Mth.wrapDegrees(this.cameraYRotOffset + scaledYRot);
				return true;
			}
			
			float cameraXRot = Mth.clamp(this.cameraXRot + scaledXRot, -90.0F, 90.0F);
			float cameraYRot = this.cameraYRot + scaledYRot;
			
			if(player.isPassenger())
			{
				Entity vehicle = player.getVehicle();
				float partialTick = Minecraft.getInstance().getFrameTime();
				
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
				player.xRotO = this.cameraXRot;
				player.yRotO = this.cameraYRot;
				player.yHeadRot = cameraYRot;
				player.yHeadRotO = this.cameraYRot;
				player.yBodyRot = cameraYRot;
				player.yBodyRotO = this.cameraYRot;
				
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
			}
			
			if(Config.CLIENT.isCameraDecoupled() && (instance.isAiming() && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying()))
			{
				player.setXRot(cameraXRot);
				player.setYRot(cameraYRot);
			}
			
			this.cameraXRot = cameraXRot;
			this.cameraYRot = cameraYRot;
			
			return Config.CLIENT.isCameraDecoupled();
		}
		
		return false;
	}
	
	public void resetState(Entity entity)
	{
		this.cameraXRot = entity.getXRot();
		this.cameraYRot = entity.getYRot();
		this.targetCameraDistance = ShoulderInstance.getInstance().getOffset().length();
		this.maxCameraDistance = this.targetCameraDistance;
		this.maxCameraDistanceO = this.targetCameraDistance;
		this.cameraXRotOffset = 0F;
		this.cameraYRotOffset = 0F;
		this.cameraXRotOffsetO = 0F;
		this.cameraYRotOffsetO = 0F;
		this.lastTranslation = Vec2f.ZERO;
		this.translation = Vec2f.ZERO;
		this.projected = null;
	}
	
	public void appendDebugText(List<String> left)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Minecraft.getInstance().showOnlyReducedInfo() && Config.CLIENT.isCameraDecoupled())
		{
			int index = findFacingDebugTextIndex(left);
			
			if(index != -1)
			{
				Direction direction = Direction.fromYRot(this.cameraYRot);
				String axis = switch(direction)
				{
					case NORTH -> "Towards negative Z";
					case SOUTH -> "Towards positive Z";
					case WEST -> "Towards negative X";
					case EAST -> "Towards positive X";
					default -> "Invalid";
				};
				float yRot = Mth.wrapDegrees(this.cameraYRot);
				float xRot = Mth.wrapDegrees(this.cameraXRot);
				left.add(index + 1, String.format(Locale.ROOT, "Camera: %s (%s) (%.1f / %.1f)", direction, axis, yRot, xRot));
			}
		}
	}
	
	private static int findFacingDebugTextIndex(List<String> left)
	{
		for(int x = 0; x < left.size(); x++)
		{
			if(left.get(x).startsWith("Facing: "))
			{
				return x;
			}
		}
		
		return -1;
	}
	
	public double getPlayerReach()
	{
		return Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public double getCameraOffsetX()
	{
		return this.cameraOffsetX;
	}
	
	public double getCameraOffsetY()
	{
		return this.cameraOffsetY;
	}
	
	public double getCameraOffsetZ()
	{
		return this.cameraOffsetZ;
	}
	
	public float getCameraEntityAlpha()
	{
		return this.cameraEntityAlpha;
	}
	
	public float getCameraXRot()
	{
		return this.cameraXRot;
	}
	
	public void setCameraXRot(float cameraXRot)
	{
		this.cameraXRot = cameraXRot;
	}
	
	public float getCameraYRot()
	{
		return this.cameraYRot;
	}
	
	public void setCameraYRot(float cameraYRot)
	{
		this.cameraYRot = cameraYRot;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
}
