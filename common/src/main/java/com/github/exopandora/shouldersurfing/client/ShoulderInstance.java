package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShoulderInstance
{
	private static final ShoulderInstance INSTANCE = new ShoulderInstance();
	private boolean doShoulderSurfing;
	private boolean isTemporaryFirstPerson;
	private boolean isAiming;
	private double offsetX = Config.CLIENT.getOffsetX();
	private double offsetY = Config.CLIENT.getOffsetY();
	private double offsetZ = Config.CLIENT.getOffsetZ();
	private double lastOffsetX = Config.CLIENT.getOffsetX();
	private double lastOffsetY = Config.CLIENT.getOffsetY();
	private double lastOffsetZ = Config.CLIENT.getOffsetZ();
	private double targetOffsetX = Config.CLIENT.getOffsetX();
	private double targetOffsetY = Config.CLIENT.getOffsetY();
	private double targetOffsetZ = Config.CLIENT.getOffsetZ();
	private boolean isFreeLooking = false;
	private float freeLookYRot = 0.0F;
	
	private ShoulderInstance()
	{
		super();
	}
	
	public void tick()
	{
		if(!Perspective.FIRST_PERSON.equals(Perspective.current()))
		{
			this.isTemporaryFirstPerson = false;
		}
		
		if(Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming) && this.doShoulderSurfing)
		{
			this.changePerspective(Perspective.FIRST_PERSON);
			this.isTemporaryFirstPerson = true;
		}
		else if(!Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming) && Perspective.FIRST_PERSON.equals(Perspective.current()) && this.isTemporaryFirstPerson)
		{
			this.changePerspective(Perspective.SHOULDER_SURFING);
		}
		
		this.lastOffsetX = this.offsetX;
		this.lastOffsetY = this.offsetY;
		this.lastOffsetZ = this.offsetZ;
		
		this.offsetX = this.lastOffsetX + (this.targetOffsetX - this.lastOffsetX) * Config.CLIENT.getCameraTransitionSpeedMultiplier();		
		this.offsetY = this.lastOffsetY + (this.targetOffsetY - this.lastOffsetY) * Config.CLIENT.getCameraTransitionSpeedMultiplier();
		this.offsetZ = this.lastOffsetZ + (this.targetOffsetZ - this.lastOffsetZ) * Config.CLIENT.getCameraTransitionSpeedMultiplier();
		
		this.isFreeLooking = KeyHandler.FREE_LOOK.isDown() && !this.isAiming;
		
		if(!this.isFreeLooking)
		{
			this.freeLookYRot = ShoulderRenderer.getInstance().getCameraYRot();
		}
	}
	
	private boolean shouldEntityAimAtTarget(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return this.isAiming && Config.CLIENT.getCrosshairType().isAimingDecoupled() || !this.isAiming && Config.CLIENT.isCameraDecoupled() &&
			(cameraEntity.isUsingItem() && cameraEntity.getUseItem().getItem().getFoodProperties() == null ||
				(minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem() || minecraft.options.keyAttack.isDown() || minecraft.options.keyPickItem.isDown()));
	}
	
	public Vec2f impulse(float leftImpulse, float forwardImpulse)
	{
		this.isAiming = ShoulderHelper.isHoldingAdaptiveItem();
		Vec2f impulse = new Vec2f(leftImpulse, forwardImpulse);
		Minecraft minecraft = Minecraft.getInstance();
		
		if(this.doShoulderSurfing && this.isFreeLooking)
		{
			return impulse.rotateDegrees(Mth.degreesDifference(minecraft.getCameraEntity().getYRot(), this.freeLookYRot));
		}
		else if(this.doShoulderSurfing && minecraft.player != null && minecraft.getCameraEntity() == minecraft.player)
		{
			LivingEntity cameraEntity = minecraft.player;
			ShoulderRenderer renderer = ShoulderRenderer.getInstance();
			boolean shouldAimAtTarget = this.shouldEntityAimAtTarget(cameraEntity, minecraft);
			boolean hasImpulse = impulse.lengthSquared() > 0;
			float xRot = cameraEntity.getXRot();
			float yRot = cameraEntity.getYRot();
			float yRotO = yRot;
			
			if(shouldAimAtTarget)
			{
				Camera camera = minecraft.gameRenderer.getMainCamera();
				double rayTraceDistance = Config.CLIENT.getCrosshairType().isAimingDecoupled() ? 400 : Config.CLIENT.getCustomRaytraceDistance();
				boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(cameraEntity);
				HitResult hitResult = ShoulderHelper.traceBlocksAndEntities(camera, minecraft.gameMode, rayTraceDistance, ClipContext.Fluid.NONE, 1.0F, true, !isCrosshairDynamic);
				Vec3 eyePosition = cameraEntity.getEyePosition();
				double dx = hitResult.getLocation().x - eyePosition.x;
				double dy = hitResult.getLocation().y - eyePosition.y;
				double dz = hitResult.getLocation().z - eyePosition.z;
				double xz = Math.sqrt(dx * dx + dz * dz);
				xRot = (float) Mth.wrapDegrees(-Mth.atan2(dy, xz) * Mth.RAD_TO_DEG);
				yRot = (float) Mth.wrapDegrees(Mth.atan2(dz, dx) * Mth.RAD_TO_DEG - 90.0F);
			}
			else if(Config.CLIENT.isCameraDecoupled() && (this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || cameraEntity.isFallFlying()) || !Config.CLIENT.isCameraDecoupled())
			{
				xRot = renderer.getCameraXRot();
				yRot = renderer.getCameraYRot();
			}
			else if(hasImpulse)
			{
				float cameraXRot = renderer.getCameraXRot();
				float cameraYRot = renderer.getCameraYRot();
				Vec2f rotated = impulse.rotateDegrees(cameraYRot);
				xRot = cameraXRot * 0.5F;
				yRot = (float) Mth.wrapDegrees(Math.atan2(-rotated.x(), rotated.y()) * Mth.RAD_TO_DEG);
				yRot = yRotO + Mth.degreesDifference(yRotO, yRot) * 0.25F;
			}
			
			if(hasImpulse)
			{
				impulse = impulse.rotateDegrees(Mth.degreesDifference(yRot, renderer.getCameraYRot()));
			}
			
			cameraEntity.setXRot(xRot);
			cameraEntity.setYRot(yRot);
		}
		
		return impulse;
	}
	
	public void changePerspective(Perspective perspective)
	{
		Minecraft.getInstance().options.setCameraType(perspective.getCameraType());
		this.doShoulderSurfing = Perspective.SHOULDER_SURFING.equals(perspective);
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(this.doShoulderSurfing && cameraEntity != null)
		{
			ShoulderRenderer.getInstance().resetState(cameraEntity);
		}
	}
	
	public boolean isCrosshairDynamic(Entity entity)
	{
		return this.doShoulderSurfing && Config.CLIENT.getCrosshairType().isDynamic(entity, this.isAiming);
	}
	
	public boolean doShoulderSurfing()
	{
		return this.doShoulderSurfing;
	}
	
	public void setShoulderSurfing(boolean doShoulderSurfing)
	{
		this.doShoulderSurfing = doShoulderSurfing;
	}
	
	public boolean isAiming()
	{
		return this.isAiming;
	}
	
	public Vec3 getOffset()
	{
		return new Vec3(this.getOffsetX(), this.getOffsetZ(), this.getOffsetY());
	}
	
	public double getOffsetX()
	{
		return this.offsetX;
	}
	
	public double getOffsetXOld()
	{
		return this.lastOffsetX;
	}
	
	public double getOffsetY()
	{
		return this.offsetY;
	}
	
	public double getOffsetYOld()
	{
		return this.lastOffsetY;
	}
	
	public double getOffsetZ()
	{
		return this.offsetZ;
	}
	
	public double getOffsetZOld()
	{
		return this.lastOffsetZ;
	}
	
	public void setTargetOffsetX(double targetOffsetX)
	{
		this.targetOffsetX = targetOffsetX;
	}
	
	public void setTargetOffsetY(double targetOffsetY)
	{
		this.targetOffsetY = targetOffsetY;
	}
	
	public void setTargetOffsetZ(double targetOffsetZ)
	{
		this.targetOffsetZ = targetOffsetZ;
	}
	
	public boolean isFreeLooking()
	{
		return this.isFreeLooking;
	}
	
	public static ShoulderInstance getInstance()
	{
		return INSTANCE;
	}
}
