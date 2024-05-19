package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.impl.ShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.Perspective;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixinducks.GameSettingsDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
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
	private boolean isFreeLooking;
	private float freeLookYRot;
	
	private ShoulderInstance()
	{
		super();
	}
	
	public void init()
	{
		if(Config.CLIENT.doRememberLastPerspective())
		{
			this.changePerspective(Config.CLIENT.getDefaultPerspective());
		}
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
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		this.isAiming = isHoldingAdaptiveItem(minecraft, minecraft.getCameraEntity());
		
		if(this.doShoulderSurfing && !this.isFreeLooking && player != null && minecraft.getCameraEntity() == player)
		{
			if(this.shouldEntityAimAtTarget(player, minecraft))
			{
				ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
				double rayTraceDistance = Config.CLIENT.getCrosshairType().isAimingDecoupled() ? 400 : Config.CLIENT.getCustomRaytraceDistance();
				boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(player);
				RayTraceResult hitResult = ShoulderRayTracer.traceBlocksAndEntities(camera, minecraft.gameMode, rayTraceDistance, RayTraceContext.FluidMode.NONE, 1.0F, true, !isCrosshairDynamic);
				float yHeadRot = player.yHeadRot;
				float yHeadRotO = player.yHeadRotO;
				float yBodyRot = player.yBodyRot;
				float yBodyRotO = player.yBodyRotO;
				float xRotO = player.xRotO;
				float yRotO = player.yRotO;
				player.lookAt(EntityAnchorArgument.Type.EYES, hitResult.getLocation());
				player.yHeadRot = yHeadRot;
				player.yHeadRotO = yHeadRotO;
				player.yBodyRot = yBodyRot;
				player.yBodyRotO = yBodyRotO;
				player.xRotO = xRotO;
				player.yRotO = yRotO;
				player.connection.send(new CPlayerPacket.RotationPacket(player.yRot, player.xRot, player.isOnGround()));
			}
			else if(this.shouldEntityFollowCamera(player))
			{
				ShoulderRenderer renderer = ShoulderRenderer.getInstance();
				player.xRot = renderer.getCameraXRot();
				player.yRot = renderer.getCameraYRot();
			}
		}
	}
	
	public void onMovementInputUpdate(MovementInput input)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		Vec2f moveVector = new Vec2f(input.leftImpulse, input.forwardImpulse);
		
		if(this.doShoulderSurfing && this.isFreeLooking)
		{
			moveVector.rotateDegrees(MathHelper.degreesDifference(cameraEntity.yRot, this.freeLookYRot));
			input.leftImpulse = moveVector.getX();
			input.forwardImpulse = moveVector.getY();
		}
		else if(this.doShoulderSurfing && minecraft.player != null && cameraEntity == minecraft.player)
		{
			if(moveVector.lengthSquared() > 0)
			{
				LivingEntity player = minecraft.player;
				ShoulderRenderer renderer = ShoulderRenderer.getInstance();
				float yRot = player.yRot;
				
				if(!this.shouldEntityAimAtTarget(player, minecraft) && !this.shouldEntityFollowCamera(player))
				{
					float yRotO = yRot;
					float cameraXRot = renderer.getCameraXRot();
					float cameraYRot = renderer.getCameraYRot();
					Vec2f rotated = moveVector.rotateDegrees(cameraYRot);
					yRot = (float) MathHelper.wrapDegrees(Math.atan2(-rotated.getX(), rotated.getY()) * ShoulderHelper.RAD_TO_DEG);
					yRot = yRotO + MathHelper.degreesDifference(yRotO, yRot) * 0.25F;
					player.xRot = cameraXRot * 0.5F;
					player.yRot = yRot;
				}
				
				moveVector = moveVector.rotateDegrees(MathHelper.degreesDifference(yRot, renderer.getCameraYRot()));
			}
			
			input.leftImpulse = moveVector.getX();
			input.forwardImpulse = moveVector.getY();
		}
	}
	
	private boolean shouldEntityAimAtTarget(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return this.isAiming && Config.CLIENT.getCrosshairType().isAimingDecoupled() || !this.isAiming && Config.CLIENT.isCameraDecoupled() &&
			(isUsingItem(cameraEntity) || !cameraEntity.isFallFlying() && (isInteracting(cameraEntity, minecraft) || isAttacking(minecraft) || isPicking(minecraft)));
	}
	
	private static boolean isUsingItem(LivingEntity cameraEntity)
	{
		return Config.CLIENT.doTurnPlayerWhenUsingItem() && cameraEntity.isUsingItem() && !cameraEntity.getUseItem().isEdible();
	}
	
	private static boolean isInteracting(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return Config.CLIENT.doTurnPlayerWhenInteracting() && minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem() &&
			(!Config.CLIENT.doRequireTargetTurningPlayerWhenInteracting() || hasTarget(minecraft));
	}
	
	private static boolean isAttacking(Minecraft minecraft)
	{
		return Config.CLIENT.doTurnPlayerWhenAttacking() && minecraft.options.keyAttack.isDown() &&
			(!Config.CLIENT.doRequireTargetTurningPlayerWhenAttacking() || hasTarget(minecraft));
	}
	
	private static boolean isPicking(Minecraft minecraft)
	{
		return Config.CLIENT.doTurnPlayerWhenPicking() && minecraft.options.keyPickItem.isDown() &&
			(!Config.CLIENT.doRequireTargetTurningPlayerWhenPicking() || hasTarget(minecraft));
	}
	
	private static boolean hasTarget(Minecraft minecraft)
	{
		return minecraft.hitResult != null && minecraft.hitResult.getType() != RayTraceResult.Type.MISS;
	}
	
	private boolean shouldEntityFollowCamera(LivingEntity cameraEntity)
	{
		return !Config.CLIENT.isCameraDecoupled() || (this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || cameraEntity.isFallFlying());
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
		((GameSettingsDuck) Minecraft.getInstance().options).shouldersurfing$setCameraTypeDirect(perspective.getCameraType());
		this.setShoulderSurfing(Perspective.SHOULDER_SURFING.equals(perspective));
	}
	
	private void onShoulderSurfingActivated()
	{
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null)
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
		if(!this.doShoulderSurfing && doShoulderSurfing)
		{
			this.onShoulderSurfingActivated();
		}
		
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
