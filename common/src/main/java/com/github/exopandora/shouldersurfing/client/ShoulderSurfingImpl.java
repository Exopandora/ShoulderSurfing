package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinducks.OptionsDuck;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ShoulderSurfingImpl implements IShoulderSurfing
{
	private final ShoulderSurfingCamera camera = new ShoulderSurfingCamera(this);
	private final CameraEntityRenderer playerRenderer = new CameraEntityRenderer(this);
	private final CrosshairRenderer crosshairRenderer = new CrosshairRenderer(this);
	private final ObjectPicker objectPicker = new ObjectPicker();
	private final InputHandler inputHandler = new InputHandler(this);
	private boolean isShoulderSurfing;
	private boolean isTemporaryFirstPerson;
	private boolean isAiming;
	private boolean isCameraDecoupled;
	private boolean isFreeLooking;
	private int turningLockTime;
	private boolean updatePlayerRotations;
	private float playerXRotO;
	private float playerYRotO;
	private boolean isLookFollowingCrosshairTarget;
	
	public void init()
	{
		Perspective targetPerspective = Config.CLIENT.getDefaultPerspective();
		
		if(!targetPerspective.isEnabled(Config.CLIENT))
		{
			targetPerspective = targetPerspective.next(Config.CLIENT);
		}
		
		if(Perspective.current() != targetPerspective)
		{
			this.changePerspective(targetPerspective);
		}
	}
	
	public void tick()
	{
		if(Config.CLIENT.requiresSaving())
		{
			Config.CLIENT.save();
		}
		
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.screen == null)
		{
			this.inputHandler.tick();
		}
		
		boolean isFirstPerson = Perspective.FIRST_PERSON == Perspective.current();
		
		if(!isFirstPerson)
		{
			this.isTemporaryFirstPerson = false;
		}
		
		Entity cameraEntity = minecraft.getCameraEntity();
		this.isAiming = PlayerStateHelper.isHoldingAdaptiveItem(minecraft, minecraft.getCameraEntity());
		this.updatePlayerRotations = false;
		LocalPlayer player = minecraft.player;
		
		if(this.isShoulderSurfing && Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming))
		{
			this.changePerspective(Perspective.FIRST_PERSON);
			this.isTemporaryFirstPerson = true;
		}
		else if(this.isTemporaryFirstPerson && isFirstPerson && !Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming))
		{
			this.changePerspective(Perspective.SHOULDER_SURFING);
		}
		
		this.isCameraDecoupled = computeIsCameraDecoupled(cameraEntity, minecraft, this.isShoulderSurfing, this.isAiming);
		
		if(this.isShoulderSurfing && player != null)
		{
			this.isLookFollowingCrosshairTarget = computeIsLookFollowingCrosshairTarget(cameraEntity, minecraft, this.isAiming);
			this.isFreeLooking = InputHandler.FREE_LOOK.isDown() && !this.isAiming;
			this.camera.tick();
			
			if(!this.isFreeLooking && cameraEntity == player)
			{
				if(this.isLookFollowingCrosshairTarget())
				{
					this.turningLockTime = this.isLookFollowingCrosshairTarget ? Config.CLIENT.getTurningLockTime() : (this.turningLockTime - 1);
					this.lookAtCrosshairTargetInternal();
				}
				else if(!this.isCameraDecoupled)
				{
					player.setXRot(this.camera.getXRot());
					player.setYRot(this.camera.getYRot());
				}
			}
		}
		
		ShoulderSurfingRegistrar.getInstance().getTickableCallbacks().forEach(ITickableCallback::tick);
	}
	
	public void lookAtCrosshairTarget()
	{
		this.turningLockTime = Config.CLIENT.getTurningLockTime();
		this.lookAtCrosshairTargetInternal();
	}
	
	private void lookAtCrosshairTargetInternal()
	{
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		assert player != null;
		Camera camera = minecraft.gameRenderer.getMainCamera();
		double interactionRange = Config.CLIENT.getCrosshairType().isAimingDecoupled() ? 400 : Config.CLIENT.getCustomRaytraceDistance();
		PickContext pickContext = new PickContext.Builder(camera).build();
		HitResult hitResult = this.objectPicker.pick(pickContext, interactionRange, 1.0F, player);
		this.playerXRotO = player.getXRot();
		this.playerYRotO = player.getYRot();
		this.updatePlayerRotations = true;
		EntityHelper.lookAtTarget(player, hitResult.getLocation());
		this.camera.setLastMovedYRot(player.getYRot());
	}
	
	public void updatePlayerRotations()
	{
		LocalPlayer player = Minecraft.getInstance().player;
		
		if(this.updatePlayerRotations && player != null)
		{
			player.xRotO = this.playerXRotO;
			player.yRotO = this.playerYRotO;
		}
	}
	
	private static boolean computeIsCameraDecoupled(@Nullable Entity cameraEntity, Minecraft minecraft, boolean isShoulderSurfing, boolean isAiming)
	{
		if(cameraEntity instanceof LivingEntity living)
		{
			if(living.isFallFlying())
			{
				return false;
			}
			else if(living.isSleeping())
			{
				return true;
			}
		}
		
		if(isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled())
		{
			return false;
		}
		
		return isShoulderSurfing && Config.CLIENT.isCameraDecoupled() && !isForcingCoupledCamera(minecraft);
	}
	
	private static boolean computeIsLookFollowingCrosshairTarget(@Nullable Entity cameraEntity, Minecraft minecraft, boolean isAiming)
	{
		if(cameraEntity instanceof LivingEntity living)
		{
			if(shouldTurnWhenInteracting(living, minecraft))
			{
				return true;
			}
			else if(Config.CLIENT.getCrosshairType().isAimingDecoupled())
			{
				if(isAiming)
				{
					return true;
				}
				else if(shouldTurnWhenUsingItem(living, minecraft))
				{
					return true;
				}
				else if(shouldTurnWhenAttacking(living, minecraft))
				{
					return true;
				}
				
				return shouldTurnWhenPicking(living, minecraft);
			}
		}
		
		return false;
	}
	
	protected static boolean shouldTurnWhenUsingItem(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return Config.CLIENT.getTurningModeWhenUsingItem().shouldTurn(minecraft.hitResult) && PlayerStateHelper.isUsingItem(cameraEntity, minecraft);
	}
	
	protected static boolean shouldTurnWhenInteracting(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return Config.CLIENT.getTurningModeWhenInteracting().shouldTurn(minecraft.hitResult) && PlayerStateHelper.isInteracting(cameraEntity, minecraft);
	}
	
	protected static boolean shouldTurnWhenAttacking(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return Config.CLIENT.getTurningModeWhenAttacking().shouldTurn(minecraft.hitResult) && PlayerStateHelper.isAttacking(cameraEntity, minecraft);
	}
	
	protected static boolean shouldTurnWhenPicking(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return Config.CLIENT.getTurningModeWhenPicking().shouldTurn(minecraft.hitResult) && PlayerStateHelper.isPicking(cameraEntity, minecraft);
	}
	
	private static boolean isForcingCoupledCamera(Minecraft minecraft)
	{
		for(ICameraCouplingCallback callback : ShoulderSurfingRegistrar.getInstance().getCameraCouplingCallbacks())
		{
			if(callback.isForcingCameraCoupling(minecraft))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void changePerspective(Perspective perspective)
	{
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		boolean wasShoulderSurfing = this.isShoulderSurfing;
		boolean isShoulderSurfing = perspective == Perspective.SHOULDER_SURFING;
		boolean isEnteringShoulderSurfing = !wasShoulderSurfing && isShoulderSurfing;
		boolean isExitingShoulderSurfing = wasShoulderSurfing && !isShoulderSurfing;
		Entity cameraEntity = minecraft.getCameraEntity();
		
		if(isExitingShoulderSurfing && player != null && cameraEntity == player)
		{
			this.lookAtCrosshairTargetInternal();
		}
		
		((OptionsDuck) minecraft.options).shouldersurfing$setCameraTypeDirect(perspective.getCameraType());
		this.isShoulderSurfing = isShoulderSurfing;
		
		if(minecraft.level != null)
		{
			minecraft.levelRenderer.needsUpdate();
		}
		
		if(isEnteringShoulderSurfing)
		{
			this.resetState();
		}
	}
	
	@Override
	public void togglePerspective()
	{
		Minecraft minecraft = Minecraft.getInstance();
		Perspective current = Perspective.current();
		Perspective next = current.next(Config.CLIENT);
		
		this.changePerspective(next);
		boolean isFirstPerson = next.getCameraType().isFirstPerson();
		
		if(current.getCameraType().isFirstPerson() != isFirstPerson)
		{
			minecraft.gameRenderer.checkEntityPostEffect(isFirstPerson ? minecraft.getCameraEntity() : null);
		}
		
		if(Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(next);
		}
	}
	
	public boolean isLookFollowingCrosshairTarget()
	{
		return this.turningLockTime > 0 || this.isLookFollowingCrosshairTarget;
	}
	
	public void toggleCameraCoupling()
	{
		Config.CLIENT.toggleCameraCoupling();
	}
	
	public void toggleOffsetXPreset()
	{
		Config.CLIENT.toggleOffsetXPreset();
	}
	
	public void toggleOffsetYPreset()
	{
		Config.CLIENT.toggleOffsetYPreset();
	}
	
	public void toggleOffsetZPreset()
	{
		Config.CLIENT.toggleOffsetZPreset();
	}
	
	public boolean isTemporaryFirstPerson()
	{
		return this.isTemporaryFirstPerson;
	}
	
	@Override
	public void swapShoulder()
	{
		Config.CLIENT.swapShoulder();
	}
	
	@Override
	public boolean isShoulderSurfing()
	{
		return this.isShoulderSurfing;
	}
	
	@Override
	public boolean isAiming()
	{
		return this.isAiming;
	}

	@Override
	public boolean isCameraDecoupled()
	{
		return this.isCameraDecoupled;
	}
	
	@Override
	public boolean isFreeLooking()
	{
		return this.isFreeLooking && this.isShoulderSurfing;
	}
	
	@Override
	public ShoulderSurfingCamera getCamera()
	{
		return this.camera;
	}
	
	@Override
	public CameraEntityRenderer getCameraEntityRenderer()
	{
		return this.playerRenderer;
	}
	
	@Override
	public CrosshairRenderer getCrosshairRenderer()
	{
		return this.crosshairRenderer;
	}
	
	@Override
	public ObjectPicker getObjectPicker()
	{
		return this.objectPicker;
	}
	
	@Override
	public IClientConfig getClientConfig()
	{
		return Config.CLIENT;
	}
	
	public InputHandler getInputHandler()
	{
		return this.inputHandler;
	}
	
	@Override
	public void resetState()
	{
		this.camera.resetState();
		this.crosshairRenderer.resetState();
		this.turningLockTime = 0;
	}
	
	public static ShoulderSurfingImpl getInstance()
	{
		return (ShoulderSurfingImpl) ShoulderSurfing.getInstance();
	}
}
