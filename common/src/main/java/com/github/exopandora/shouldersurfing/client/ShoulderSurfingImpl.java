package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.IAttackStateCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.CrosshairType;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.api.model.PickVector;
import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinducks.OptionsDuck;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

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
		
		this.isAiming = isHoldingAdaptiveItem(minecraft, minecraft.getCameraEntity());
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
		
		this.isCameraDecoupled = this.isShoulderSurfing && Config.CLIENT.isCameraDecoupled() && !isForcingCoupledCamera(minecraft);
		
		if(this.isShoulderSurfing && player != null)
		{
			boolean isTurningLockActive = this.turningLockTime > 0;
			
			if(isTurningLockActive && !this.isCameraDecoupled())
			{
				this.turningLockTime = 0;
			}
			
			this.isFreeLooking = InputHandler.FREE_LOOK.isDown() && !this.isAiming;
			this.camera.tick();
			
			if(!this.isFreeLooking && minecraft.getCameraEntity() == player)
			{
				boolean shouldAimAtTarget = this.shouldEntityAimAtTargetInternal(player, minecraft);
				
				if(shouldAimAtTarget || isTurningLockActive)
				{
					this.turningLockTime = shouldAimAtTarget ? Config.CLIENT.getTurningLockTime() : (this.turningLockTime - 1);
					this.lookAtCrosshairTargetInternal();
				}
				else if(this.shouldEntityFollowCamera(player))
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
	
	private boolean shouldEntityAimAtTargetInternal(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return this.isAiming && Config.CLIENT.getCrosshairType().isAimingDecoupled() || !this.isAiming && this.isCameraDecoupled() &&
			(isUsingItem(cameraEntity, minecraft) || !cameraEntity.isFallFlying() && (isInteracting(cameraEntity, minecraft) &&
				!(Config.CLIENT.getPickVector() == PickVector.PLAYER && Config.CLIENT.getCrosshairType() == CrosshairType.DYNAMIC) ||
				isAttacking(minecraft) || isPicking(minecraft) || cameraEntity instanceof Player player && player.isScoping()));
	}
	
	public boolean shouldEntityAimAtTarget(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return this.turningLockTime > 0 || this.shouldEntityAimAtTargetInternal(cameraEntity, minecraft);
	}
	
	public boolean isEntityRotationDecoupled(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return !this.shouldEntityAimAtTarget(cameraEntity, minecraft) && !this.shouldEntityFollowCamera(cameraEntity);
	}
	
	private static boolean isUsingItem(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return cameraEntity.isUsingItem() && Config.CLIENT.getTurningModeWhenUsingItem().shouldTurn(minecraft.hitResult) &&
			!cameraEntity.getUseItem().has(DataComponents.FOOD);
	}
	
	private static boolean isInteracting(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem() &&
			Config.CLIENT.getTurningModeWhenInteracting().shouldTurn(minecraft.hitResult);
	}
	
	private static boolean isAttacking(Minecraft minecraft)
	{
		final TurningMode turningMode = Config.CLIENT.getTurningModeWhenAttacking();
		final boolean defaultIsAttacking = minecraft.options.keyAttack.isDown() && turningMode.shouldTurn(minecraft.hitResult);
		
		for(IAttackStateCallback callback : ShoulderSurfingRegistrar.getInstance().getAttackStateCallbacks())
		{
			IAttackStateCallback.Result result = callback.isAttacking(new IAttackStateCallback.Context(minecraft, turningMode, defaultIsAttacking));
			switch (result)
			{
				case TRUE -> { return true; }
				case FALSE -> { return false; }
				case PASS -> { /* Continue to next callback */ }
			}
		}
		return defaultIsAttacking;
	}
	
	private static boolean isPicking(Minecraft minecraft)
	{
		return minecraft.options.keyPickItem.isDown() && Config.CLIENT.getTurningModeWhenPicking().shouldTurn(minecraft.hitResult);
	}
	
	public boolean shouldEntityFollowCamera(LivingEntity cameraEntity)
	{
		return (this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || cameraEntity.isFallFlying()) ||
			!this.isCameraDecoupled();
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, Entity entity)
	{
		if(entity instanceof LivingEntity living)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, living));
		}
		
		return false;
	}
	
	private static boolean isForcingCoupledCamera(Minecraft minecraft)
	{
		return ShoulderSurfingRegistrar.getInstance().getCameraCouplingCallbacks().stream().anyMatch(callback -> callback.isForcingCameraCoupling(minecraft));
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
