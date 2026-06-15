package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.api.config.IClientConfig;
import com.github.exopandora.shouldersurfing.api.util.EntityHelper;
import com.github.exopandora.shouldersurfing.client.renderer.CameraEntityRenderer;
import com.github.exopandora.shouldersurfing.client.renderer.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.world.phys.ObjectPicker;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.PerspectiveConfig;
import com.github.exopandora.shouldersurfing.config.PlayerConfig;
import com.github.exopandora.shouldersurfing.event.EventBus;
import com.github.exopandora.shouldersurfing.mixinduck.OptionsDuck;
import com.github.exopandora.shouldersurfing.plugin.PluginLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ShoulderSurfing implements IShoulderSurfing {
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
	private EventBus eventBus;
	
	public void init() {
		PluginLoader.getInstance().loadPlugins();
		this.eventBus = EventBus.create(PluginLoader.getInstance().getPluginContainers());
		PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
		Perspective targetPerspective = perspectiveConfig.getDefaultPerspective();
		if (!targetPerspective.isEnabled(perspectiveConfig)) {
			targetPerspective = targetPerspective.next(perspectiveConfig);
		}
		if (Perspective.current() != targetPerspective) {
			this.changePerspective(targetPerspective);
		}
	}
	
	public void tick() {
		if (Config.CLIENT.requiresSaving()) {
			Config.CLIENT.save();
		}
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen == null) {
			this.inputHandler.tick();
		}
		boolean isFirstPerson = Perspective.FIRST_PERSON == Perspective.current();
		if (!isFirstPerson) {
			this.isTemporaryFirstPerson = false;
		}
		Entity cameraEntity = minecraft.getCameraEntity();
		this.isAiming = computeIsAiming(cameraEntity);
		if (this.isShoulderSurfing) {
			if (EventHooks.isTemporaryFirstPerson()) {
				this.changePerspective(Perspective.FIRST_PERSON);
				this.isTemporaryFirstPerson = true;
			}
		} else if (this.isTemporaryFirstPerson && isFirstPerson) {
			if (!EventHooks.isTemporaryFirstPerson()) {
				this.changePerspective(Perspective.SHOULDER_SURFING);
			}
		}
		LocalPlayer player = minecraft.player;
		this.updatePlayerRotations = false;
		this.isCameraDecoupled = computeIsCameraDecoupled(cameraEntity, this.isShoulderSurfing, this.isAiming);
		if (this.isShoulderSurfing && player != null) {
			this.isLookFollowingCrosshairTarget = computeIsLookFollowingCrosshairTarget(cameraEntity, this.isAiming);
			this.isFreeLooking = InputHandler.FREE_LOOK.isDown() && !this.isAiming;
			this.camera.tick();
			if (!this.isFreeLooking && cameraEntity == player) {
				if (this.isLookFollowingCrosshairTarget()) {
					this.turningLockTime = this.isLookFollowingCrosshairTarget
						? Config.CLIENT.getPlayerConfig().getTurningLockTime()
						: (this.turningLockTime - 1);
					this.lookAtCrosshairTargetInternal();
				} else if (!this.isCameraDecoupled) {
					player.setXRot(this.camera.getXRot());
					player.setYRot(this.camera.getYRot());
				}
			}
		}
		EventHooks.tick();
	}
	
	private static boolean computeIsAiming(Entity cameraEntity) {
		if (!(cameraEntity instanceof LivingEntity)) {
			return false;
		}
		return EventHooks.isAiming((LivingEntity) cameraEntity);
	}
	
	public void lookAtCrosshairTarget() {
		this.turningLockTime = Config.CLIENT.getPlayerConfig().getTurningLockTime();
		this.lookAtCrosshairTargetInternal();
	}
	
	private void lookAtCrosshairTargetInternal() {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		assert player != null;
		Camera camera = minecraft.gameRenderer.getMainCamera();
		double interactionRange = Config.CLIENT.getCrosshairConfig().getCrosshairType().isAimingDecoupled()
			? 400
			: Config.CLIENT.getObjectPickerConfig().getCustomRaytraceDistance();
		PickContext pickContext = new PickContext.Builder(camera).build();
		HitResult hitResult = this.objectPicker.pick(pickContext, interactionRange, 1.0F, minecraft.gameMode);
		this.playerXRotO = player.getXRot();
		this.playerYRotO = player.getYRot();
		this.updatePlayerRotations = true;
		EntityHelper.lookAtTarget(player, hitResult.getLocation());
		this.camera.setLastMovedYRot(player.getYRot());
	}
	
	public void updatePlayerRotations() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (this.updatePlayerRotations && player != null) {
			player.xRotO = this.playerXRotO;
			player.yRotO = this.playerYRotO;
		}
	}
	
	private static boolean computeIsCameraDecoupled(@Nullable Entity cameraEntity, boolean isShoulderSurfing, boolean isAiming) {
		if (cameraEntity instanceof LivingEntity living) {
			if (living.isFallFlying()) {
				return false;
			} else if (living.isSleeping()) {
				return true;
			}
		}
		if (isAiming && !Config.CLIENT.getCrosshairConfig().getCrosshairType().isAimingDecoupled()) {
			return false;
		}
		return isShoulderSurfing && Config.CLIENT.getCameraConfig().isCameraDecoupled() && !EventHooks.isForcingCoupledCamera();
	}
	
	private static boolean computeIsLookFollowingCrosshairTarget(@Nullable Entity cameraEntity, boolean isAiming) {
		if (cameraEntity instanceof LivingEntity living) {
			if (shouldTurnWhenInteracting(living)) {
				return true;
			} else if (Config.CLIENT.getCrosshairConfig().getCrosshairType().isAimingDecoupled()) {
				if (isAiming) {
					return true;
				} else if (shouldTurnWhenUsingItem(living)) {
					return true;
				} else if (shouldTurnWhenAttacking(living)) {
					return true;
				}
				return shouldTurnWhenPicking(living);
			}
		}
		return false;
	}
	
	protected static boolean shouldTurnWhenUsingItem(LivingEntity cameraEntity) {
		HitResult hitResult = Minecraft.getInstance().hitResult;
		PlayerConfig playerConfig = Config.CLIENT.getPlayerConfig();
		return playerConfig.getTurningModeWhenUsingItem().shouldTurn(hitResult) && EventHooks.isUsingItem(cameraEntity);
	}
	
	protected static boolean shouldTurnWhenInteracting(LivingEntity cameraEntity) {
		HitResult hitResult = Minecraft.getInstance().hitResult;
		PlayerConfig playerConfig = Config.CLIENT.getPlayerConfig();
		return playerConfig.getTurningModeWhenInteracting().shouldTurn(hitResult) && EventHooks.isInteracting(cameraEntity);
	}
	
	protected static boolean shouldTurnWhenAttacking(LivingEntity cameraEntity) {
		HitResult hitResult = Minecraft.getInstance().hitResult;
		PlayerConfig playerConfig = Config.CLIENT.getPlayerConfig();
		return playerConfig.getTurningModeWhenAttacking().shouldTurn(hitResult) && EventHooks.isAttacking(cameraEntity);
	}
	
	protected static boolean shouldTurnWhenPicking(LivingEntity cameraEntity) {
		HitResult hitResult = Minecraft.getInstance().hitResult;
		PlayerConfig playerConfig = Config.CLIENT.getPlayerConfig();
		return playerConfig.getTurningModeWhenPicking().shouldTurn(hitResult) && EventHooks.isPicking(cameraEntity);
	}
	
	@Override
	public void changePerspective(Perspective perspective) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		boolean wasShoulderSurfing = this.isShoulderSurfing;
		boolean isShoulderSurfing = perspective == Perspective.SHOULDER_SURFING;
		boolean isEnteringShoulderSurfing = !wasShoulderSurfing && isShoulderSurfing;
		boolean isExitingShoulderSurfing = wasShoulderSurfing && !isShoulderSurfing;
		Entity cameraEntity = minecraft.getCameraEntity();
		if (isExitingShoulderSurfing && player != null && cameraEntity == player) {
			this.lookAtCrosshairTargetInternal();
		}
		((OptionsDuck) minecraft.options).shouldersurfing$setCameraTypeDirect(perspective.getCameraType());
		this.isShoulderSurfing = isShoulderSurfing;
		if (minecraft.level != null) {
			minecraft.levelRenderer.needsUpdate();
		}
		if (isEnteringShoulderSurfing) {
			this.resetState();
		}
	}
	
	@Override
	public void togglePerspective() {
		Minecraft minecraft = Minecraft.getInstance();
		Perspective current = Perspective.current();
		PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
		Perspective next = current.next(perspectiveConfig);
		this.changePerspective(next);
		boolean isFirstPerson = next.getCameraType().isFirstPerson();
		if (current.getCameraType().isFirstPerson() != isFirstPerson) {
			minecraft.gameRenderer.checkEntityPostEffect(isFirstPerson ? minecraft.getCameraEntity() : null);
		}
		if (perspectiveConfig.isPerspectivePersistent()) {
			perspectiveConfig.setDefaultPerspective(next);
		}
	}
	
	@Override
	public void toggleCameraCoupling() {
		Config.CLIENT.getCameraConfig().toggleCameraCoupling();
	}
	
	public void toggleOffsetXPreset() {
		Config.CLIENT.getCameraConfig().toggleOffsetXPreset();
	}
	
	public void toggleOffsetYPreset() {
		Config.CLIENT.getCameraConfig().toggleOffsetYPreset();
	}
	
	public void toggleOffsetZPreset() {
		Config.CLIENT.getCameraConfig().toggleOffsetZPreset();
	}
	
	@Override
	public void swapShoulder() {
		Config.CLIENT.getCameraConfig().swapShoulder();
	}
	
	@Override
	public boolean isLookFollowingCrosshairTarget() {
		return this.turningLockTime > 0 || this.isLookFollowingCrosshairTarget;
	}
	
	@Override
	public boolean isShoulderSurfing() {
		return this.isShoulderSurfing;
	}
	
	@Override
	public boolean isAiming() {
		return this.isAiming;
	}
	
	@Override
	public boolean isCameraDecoupled() {
		return this.isCameraDecoupled;
	}
	
	@Override
	public boolean isFreeLooking() {
		return this.isFreeLooking && this.isShoulderSurfing;
	}
	
	@Override
	public boolean isTemporaryFirstPerson() {
		return this.isTemporaryFirstPerson;
	}
	
	@Override
	public ShoulderSurfingCamera getCamera() {
		return this.camera;
	}
	
	@Override
	public CameraEntityRenderer getCameraEntityRenderer() {
		return this.playerRenderer;
	}
	
	@Override
	public CrosshairRenderer getCrosshairRenderer() {
		return this.crosshairRenderer;
	}
	
	@Override
	public ObjectPicker getObjectPicker() {
		return this.objectPicker;
	}
	
	@Override
	public IClientConfig getClientConfig() {
		return Config.CLIENT;
	}
	
	public InputHandler getInputHandler() {
		return this.inputHandler;
	}
	
	public EventBus getEventBus() {
		return this.eventBus;
	}
	
	@Override
	public void resetState() {
		this.camera.resetState();
		this.crosshairRenderer.resetState();
		this.turningLockTime = 0;
	}
	
	public static ShoulderSurfing getInstance() {
		return (ShoulderSurfing) IShoulderSurfing.getInstance();
	}
}
