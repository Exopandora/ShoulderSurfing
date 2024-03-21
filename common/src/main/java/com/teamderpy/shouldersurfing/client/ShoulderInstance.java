package com.teamderpy.shouldersurfing.client;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
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
	private boolean doSwitchPerspective;
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
	private float cameraEntityXRot = 0F;
	private float cameraEntityYRot = 0F;
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
			this.doSwitchPerspective = false;
		}
		
		this.isAiming = ShoulderHelper.isHoldingAdaptiveItem();
		
		if(Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming) && this.doShoulderSurfing)
		{
			this.changePerspective(Perspective.FIRST_PERSON);
			this.doSwitchPerspective = true;
		}
		else if(!Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming) && Perspective.FIRST_PERSON.equals(Perspective.current()) && this.doSwitchPerspective)
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
	
	public Vec2f impulse(float leftImpulse, float forwardImpulse)
	{
		Vec2f impulse = new Vec2f(leftImpulse, forwardImpulse);
		Minecraft minecraft = Minecraft.getInstance();
		
		if(this.doShoulderSurfing && this.isFreeLooking)
		{
			return impulse.rotateDegrees(Mth.degreesDifference(this.cameraEntityYRot, this.freeLookYRot));
		}
		else if(this.doShoulderSurfing && Config.CLIENT.isCameraDecoupled() && minecraft.getCameraEntity() instanceof LivingEntity cameraEntity)
		{
			boolean hasImpulse = impulse.lengthSquared() > 0;
			float cameraEntityYRotO = this.cameraEntityYRot;
			ShoulderRenderer renderer = ShoulderRenderer.getInstance();
			Options options = minecraft.options;
			
			if(this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled())
			{
				this.cameraEntityXRot = renderer.getCameraXRot();
				this.cameraEntityYRot = renderer.getCameraYRot();
			}
			else if(this.isAiming && Config.CLIENT.getCrosshairType().isAimingDecoupled() || cameraEntity.isFallFlying() || cameraEntity.isUsingItem() || (cameraEntity == minecraft.player && (options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown())))
			{
				HitResult hitResult = ShoulderHelper.traceBlocksAndEntities(minecraft.gameRenderer.getMainCamera(), minecraft.gameMode, 400, ClipContext.Fluid.NONE, 1.0F, true, true);
				Vec3 eyePosition = cameraEntity.getEyePosition();
				double dx = hitResult.getLocation().x - eyePosition.x;
				double dy = hitResult.getLocation().y - eyePosition.y;
				double dz = hitResult.getLocation().z - eyePosition.z;
				double xz = Math.sqrt(dx * dx + dz * dz);
				this.cameraEntityXRot = (float) Mth.wrapDegrees(-Mth.atan2(dy, xz) * Mth.RAD_TO_DEG);
				this.cameraEntityYRot = (float) Mth.wrapDegrees(Mth.atan2(dz, dx) * Mth.RAD_TO_DEG - 90.0F);
			}
			else if(hasImpulse)
			{
				float cameraXRot = renderer.getCameraXRot();
				float cameraYRot = renderer.getCameraYRot();
				Vec2f rotated = impulse.rotateDegrees(cameraYRot);
				this.cameraEntityXRot = cameraXRot * 0.5F;
				this.cameraEntityYRot = (float) Mth.wrapDegrees(Math.atan2(-rotated.x(), rotated.y()) * Mth.RAD_TO_DEG);
			}
			
			if(hasImpulse)
			{
				this.cameraEntityYRot = cameraEntityYRotO + Mth.degreesDifference(cameraEntityYRotO, this.cameraEntityYRot) * 0.25F;
				impulse = impulse.rotateDegrees(Mth.degreesDifference(this.cameraEntityYRot, renderer.getCameraYRot()));
			}
			
			cameraEntity.setXRot(this.cameraEntityXRot);
			cameraEntity.setYRot(this.cameraEntityYRot);
		}
		
		return impulse;
	}
	
	public void resetCameraEntityRotations(Entity entity)
	{
		this.cameraEntityXRot = entity.getXRot();
		this.cameraEntityYRot = entity.getYRot();
	}
	
	public void changePerspective(Perspective perspective)
	{
		Minecraft.getInstance().options.setCameraType(perspective.getCameraType());
		this.doShoulderSurfing = Perspective.SHOULDER_SURFING.equals(perspective);
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(this.doShoulderSurfing && cameraEntity != null)
		{
			this.resetCameraEntityRotations(cameraEntity);
			ShoulderRenderer.getInstance().resetCameraRotations(cameraEntity);
		}
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
