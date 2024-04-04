package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixins.ActiveRenderInfoAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
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
	
	public void offsetCrosshair(MatrixStack poseStack, MainWindow window, float partialTicks)
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
			poseStack.last().pose().translate(new Vector3f(this.translation.getX(), -this.translation.getY(), 0F));
			this.lastTranslation = this.translation;
		}
		else
		{
			this.lastTranslation = Vec2f.ZERO;
		}
	}
	
	public void clearCrosshairOffset(MatrixStack poseStack)
	{
		if(ShoulderInstance.getInstance().isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()) && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(ActiveRenderInfo camera, World level, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null && !(camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).isSleeping()))
		{
			ActiveRenderInfoAccessor accessor = (ActiveRenderInfoAccessor) camera;
			float cameraXRotWithOffset = MathHelper.clamp(MathHelper.rotLerp(partialTick, this.cameraXRotOffsetO, this.cameraXRotOffset) + this.cameraXRot, -90F, 90F);
			float cameraYRotWithOffset = MathHelper.rotLerp(partialTick, this.cameraYRotOffsetO, this.cameraYRotOffset) + this.cameraYRot;
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
				if(Config.CLIENT.doCenterCameraWhenClimbing() && camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).onClimbable())
				{
					targetXOffset = 0;
				}
				
				if(ShoulderHelper.angle(camera.getLookVector(), Vector3f.YN) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * ShoulderHelper.DEG_TO_RAD)
				{
					targetXOffset = 0;
					targetYOffset = 0;
				}
				
				if(Config.CLIENT.doDynamicallyAdjustOffsets())
				{
					Vector3d localCameraOffset = new Vector3d(targetXOffset, targetYOffset, targetZOffset);
					Vector3d worldCameraOffset = new Vector3d(camera.getUpVector()).scale(targetYOffset).add(new Vector3d(accessor.getLeft()).scale(targetXOffset)).add(new Vector3d(camera.getLookVector()).scale(-targetZOffset)).normalize().scale(localCameraOffset.length());
					Vector3d worldXYOffset = ShoulderHelper.calcRayTraceHeadOffset(camera, worldCameraOffset);
					Vector3d eyePosition = camera.getEntity().getEyePosition(partialTick);
					double absOffsetX = Math.abs(targetXOffset);
					double absOffsetY = Math.abs(targetYOffset);
					double absOffsetZ = Math.abs(targetZOffset);
					double targetX = absOffsetX;
					double targetY = absOffsetY;
					double clearance = Minecraft.getInstance().getCameraEntity().getBbWidth() / 3.0D;
					
					for(double dz = 0; dz <= absOffsetZ; dz += 0.03125D)
					{
						double scale = dz / absOffsetZ;
						Vector3d from = eyePosition.add(worldCameraOffset.scale(scale));
						Vector3d to = eyePosition.add(worldXYOffset).add(new Vector3d(camera.getLookVector()).scale(-dz));
						RayTraceContext context = new RayTraceContext(from, to, BlockMode.VISUAL, FluidMode.NONE, camera.getEntity());
						BlockRayTraceResult hitResult = level.clip(context);
						
						if(hitResult.getType() != Type.MISS)
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
			
			double x = MathHelper.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = MathHelper.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + MathHelper.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = MathHelper.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			double offsetX = MathHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = MathHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = MathHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vector3d offset = new Vector3d(offsetX, offsetY, offsetZ);
			
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
					this.cameraDistance = Math.min(this.targetCameraDistance, MathHelper.lerp(partialTick, this.maxCameraDistanceO, this.maxCameraDistance));
				}
				
				Vector3d scaled = offset.normalize().scale(this.cameraDistance);
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
	
	private double calcCameraDistance(ActiveRenderInfo camera, World level, double distance, float partialTick)
	{
		ShoulderInstance instance = ShoulderInstance.getInstance();
		ActiveRenderInfoAccessor accessor = (ActiveRenderInfoAccessor) camera;
		double offsetX = MathHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
		double offsetY = MathHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
		double offsetZ = MathHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
		Vector3d cameraOffset = new Vector3d(camera.getUpVector()).scale(offsetY)
			.add(new Vector3d(accessor.getLeft()).scale(offsetX))
			.add(new Vector3d(camera.getLookVector()).scale(-offsetZ))
			.normalize()
			.scale(distance);
		Vector3d eyePosition = camera.getEntity().getEyePosition(partialTick);
		
		for(int i = 0; i < 8; i++)
		{
			Vector3d offset = new Vector3d(i & 1, i >> 1 & 1, i >> 2 & 1)
				.scale(2)
				.subtract(1, 1, 1)
				.scale(0.15)
				.yRot(-camera.getYRot() * ShoulderHelper.DEG_TO_RAD);
			Vector3d from = eyePosition.add(offset);
			Vector3d to = from.add(cameraOffset);
			RayTraceContext context = new RayTraceContext(from, to, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, camera.getEntity());
			RayTraceResult hitResult = level.clip(context);
			
			if(hitResult.getType() != Type.MISS)
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
	
	public void updateDynamicRaytrace(ActiveRenderInfo camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			PlayerController gameMode = Minecraft.getInstance().gameMode;
			RayTraceResult hitResult = ShoulderHelper.traceBlocksAndEntities(camera, gameMode, this.getPlayerReach(), RayTraceContext.FluidMode.NONE, partialTick, true, false);
			Vector3d position = hitResult.getLocation().subtract(camera.getPosition());
			this.projected = ShoulderHelper.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	private boolean shouldSkipCameraEntityRendering(Entity cameraEntity)
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() && !cameraEntity.isSpectator() &&
			(this.cameraDistance < cameraEntity.getBbWidth() * Config.CLIENT.keepCameraOutOfHeadMultiplier() ||
				cameraEntity.xRot < Config.CLIENT.getCenterCameraWhenLookingDownAngle() - 90);
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick)
	{
		if(this.shouldSkipCameraEntityRendering(entity))
		{
			return true;
		}
		
		if(this.shouldRenderCameraEntityTransparent(entity))
		{
			float xAlpha = (float) MathHelper.clamp(Math.abs(this.cameraOffsetX) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			
			if(this.cameraOffsetY > 0)
			{
				yAlpha = (float) MathHelper.clamp(this.cameraOffsetY / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			}
			else if(this.cameraOffsetY < 0)
			{
				yAlpha = (float) MathHelper.clamp(-this.cameraOffsetY / -entity.getEyeHeight(), 0, 1.0F);
			}
			
			this.cameraEntityAlpha = MathHelper.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), 0.15F, 1.0F);
			System.out.println(this.cameraEntityAlpha);
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
	
	public boolean turn(PlayerEntity player, double yRot, double xRot)
	{
		ShoulderInstance instance = ShoulderInstance.getInstance();
		
		if(instance.doShoulderSurfing())
		{
			float scaledXRot = (float) (xRot * 0.15F);
			float scaledYRot = (float) (yRot * 0.15F);
			
			if(instance.isFreeLooking())
			{
				this.cameraXRotOffset = MathHelper.clamp(this.cameraXRotOffset + scaledXRot, -90.0F, 90.0F);
				this.cameraYRotOffset = MathHelper.wrapDegrees(this.cameraYRotOffset + scaledYRot);
				return true;
			}
			
			float cameraXRot = MathHelper.clamp(this.cameraXRot + scaledXRot, -90.0F, 90.0F);
			float cameraYRot = this.cameraYRot + scaledYRot;
			
			if(player.isPassenger())
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
				player.xRotO = this.cameraXRot;
				player.yRotO = this.cameraYRot;
				player.yHeadRot = cameraYRot;
				player.yHeadRotO = this.cameraYRot;
				player.yBodyRot = cameraYRot;
				player.yBodyRotO = this.cameraYRot;
				
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
			}
			
			if(Config.CLIENT.isCameraDecoupled() && (instance.isAiming() && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying()))
			{
				player.xRot = cameraXRot;
				player.yRot = cameraYRot;
			}
			
			this.cameraXRot = cameraXRot;
			this.cameraYRot = cameraYRot;
			
			return Config.CLIENT.isCameraDecoupled();
		}
		
		return false;
	}
	
	public void resetState(Entity entity)
	{
		this.cameraXRot = entity.xRot;
		this.cameraYRot = entity.yRot;
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
			int index = this.findFacingDebugTextIndex(left);
			
			if(index != -1)
			{
				Direction direction = Direction.fromYRot(this.cameraYRot);
				String axis;
				switch(direction)
				{
					case NORTH:
						axis = "Towards negative Z";
						break;
					case SOUTH:
						axis = "Towards positive Z";
						break;
					case WEST:
						axis = "Towards negative X";
						break;
					case EAST:
						axis = "Towards positive X";
						break;
					default:
						axis = "Invalid";
						break;
				}
				float yRot = MathHelper.wrapDegrees(this.cameraYRot);
				float xRot = MathHelper.wrapDegrees(this.cameraXRot);
				left.add(index + 1, String.format(Locale.ROOT, "Camera: %s (%s) (%.1f / %.1f)", direction, axis, yRot, xRot));
			}
		}
	}
	
	private int findFacingDebugTextIndex(List<String> left)
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
	
	public float getCameraYRot()
	{
		return this.cameraYRot;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
}
