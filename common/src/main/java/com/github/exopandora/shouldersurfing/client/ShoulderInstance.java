package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.impl.ShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

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
			(Config.CLIENT.doTurnPlayerWhenUsingItem() && cameraEntity.isUsingItem() && !cameraEntity.getUseItem().isEdible() ||
				!cameraEntity.isFallFlying() && minecraft.hitResult != null && minecraft.hitResult.getType() != RayTraceResult.Type.MISS &&
					(Config.CLIENT.doTurnPlayerWhenInteracting() && minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem() ||
						Config.CLIENT.doTurnPlayerWhenAttacking() && minecraft.options.keyAttack.isDown() ||
						Config.CLIENT.doTurnPlayerWhenPicking() && minecraft.options.keyPickItem.isDown()));
	}
	
	public void onMovementInputUpdate(MovementInput input)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		Vec2f moveVector = new Vec2f(input.leftImpulse, input.forwardImpulse);
		this.isAiming = isHoldingAdaptiveItem(minecraft, cameraEntity);
		
		if(this.doShoulderSurfing && this.isFreeLooking)
		{
			moveVector.rotateDegrees(MathHelper.degreesDifference(cameraEntity.yRot, this.freeLookYRot));
			input.leftImpulse = moveVector.getX();
			input.forwardImpulse = moveVector.getY();
		}
		else if(this.doShoulderSurfing && minecraft.player != null && cameraEntity == minecraft.player)
		{
			LivingEntity player = minecraft.player;
			ShoulderRenderer renderer = ShoulderRenderer.getInstance();
			boolean shouldAimAtTarget = this.shouldEntityAimAtTarget(player, minecraft);
			boolean hasImpulse = moveVector.lengthSquared() > 0;
			float xRot = player.xRot;
			float yRot = player.yRot;
			float yRotO = yRot;
			
			if(shouldAimAtTarget)
			{
				ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
				double rayTraceDistance = Config.CLIENT.getCrosshairType().isAimingDecoupled() ? 400 : Config.CLIENT.getCustomRaytraceDistance();
				boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(player);
				RayTraceResult hitResult = ShoulderRayTracer.traceBlocksAndEntities(camera, minecraft.gameMode, rayTraceDistance, RayTraceContext.FluidMode.NONE, 1.0F, true, !isCrosshairDynamic);
				Vector3d eyePosition = player.getEyePosition(1.0F);
				double dx = hitResult.getLocation().x - eyePosition.x;
				double dy = hitResult.getLocation().y - eyePosition.y;
				double dz = hitResult.getLocation().z - eyePosition.z;
				double xz = Math.sqrt(dx * dx + dz * dz);
				xRot = (float) MathHelper.wrapDegrees(-MathHelper.atan2(dy, xz) * ShoulderHelper.RAD_TO_DEG);
				yRot = (float) MathHelper.wrapDegrees(MathHelper.atan2(dz, dx) * ShoulderHelper.RAD_TO_DEG - 90.0F);
			}
			else if(Config.CLIENT.isCameraDecoupled() && (this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || player.isFallFlying()) || !Config.CLIENT.isCameraDecoupled())
			{
				xRot = renderer.getCameraXRot();
				yRot = renderer.getCameraYRot();
			}
			else if(hasImpulse)
			{
				float cameraXRot = renderer.getCameraXRot();
				float cameraYRot = renderer.getCameraYRot();
				Vec2f rotated = moveVector.rotateDegrees(cameraYRot);
				xRot = cameraXRot * 0.5F;
				yRot = (float) MathHelper.wrapDegrees(Math.atan2(-rotated.getX(), rotated.getY()) * ShoulderHelper.RAD_TO_DEG);
				yRot = yRotO + MathHelper.degreesDifference(yRotO, yRot) * 0.25F;
			}
			
			if(hasImpulse)
			{
				moveVector = moveVector.rotateDegrees(MathHelper.degreesDifference(yRot, renderer.getCameraYRot()));
			}
			
			player.xRot = xRot;
			player.yRot = yRot;
			
			input.leftImpulse = moveVector.getX();
			input.forwardImpulse = moveVector.getY();
		}
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, Entity entity)
	{
		if(entity instanceof LivingEntity)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, (LivingEntity) entity));
		}
		
		return false;
	}
	
	public void changePerspective(Perspective perspective)
	{
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.setCameraType(perspective.getCameraType());
		Entity cameraEntity = minecraft.getCameraEntity();
		this.doShoulderSurfing = Perspective.SHOULDER_SURFING.equals(perspective);
		
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
	
	public Vector3d getOffset()
	{
		return new Vector3d(this.getOffsetX(), this.getOffsetZ(), this.getOffsetY());
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
