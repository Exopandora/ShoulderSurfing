package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixinducks.OptionsDuck;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
	private boolean isFreeLooking;
	
	public void init()
	{
		if(Config.CLIENT.doRememberLastPerspective())
		{
			this.changePerspective(Config.CLIENT.getDefaultPerspective());
		}
	}
	
	public void tick()
	{
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
		LocalPlayer player = minecraft.player;
		
		if(this.isShoulderSurfing && Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming))
		{
			this.lookAtTarget(player, minecraft);
			this.changePerspective(Perspective.FIRST_PERSON);
			this.isTemporaryFirstPerson = true;
		}
		else if(this.isTemporaryFirstPerson && isFirstPerson && !Config.CLIENT.getCrosshairType().doSwitchPerspective(this.isAiming))
		{
			this.changePerspective(Perspective.SHOULDER_SURFING);
		}
		
		if(this.isShoulderSurfing && player != null)
		{
			this.isFreeLooking = InputHandler.FREE_LOOK.isDown() && !this.isAiming;
			this.camera.tick();
			
			if(!this.isFreeLooking && minecraft.getCameraEntity() == player)
			{
				if(this.shouldEntityAimAtTarget(player, minecraft))
				{
					this.lookAtTarget(player, minecraft);
				}
				else if(this.shouldEntityFollowCamera(player))
				{
					player.setXRot(this.camera.getXRot());
					player.setYRot(this.camera.getYRot());
				}
			}
		}
	}
	
	private void lookAtTarget(LocalPlayer player, Minecraft minecraft)
	{
		Camera camera = minecraft.gameRenderer.getMainCamera();
		double interactionRange = Config.CLIENT.getCrosshairType().isAimingDecoupled() ? 400 : Config.CLIENT.getCustomRaytraceDistance();
		PickContext pickContext = new PickContext.Builder(camera).build();
		HitResult hitResult = this.objectPicker.pick(pickContext, interactionRange, 1.0F, minecraft.gameMode);
		float yHeadRot = player.yHeadRot;
		float yHeadRotO = player.yHeadRotO;
		float yBodyRot = player.yBodyRot;
		float yBodyRotO = player.yBodyRotO;
		float xRotO = player.xRotO;
		float yRotO = player.yRotO;
		player.lookAt(EntityAnchorArgument.Anchor.EYES, hitResult.getLocation());
		player.yHeadRot = yHeadRot;
		player.yHeadRotO = yHeadRotO;
		player.yBodyRot = yBodyRot;
		player.yBodyRotO = yBodyRotO;
		player.xRotO = xRotO;
		player.yRotO = yRotO;
		player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
	}
	
	public boolean shouldEntityAimAtTarget(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return this.isAiming && Config.CLIENT.getCrosshairType().isAimingDecoupled() || !this.isAiming && Config.CLIENT.isCameraDecoupled() &&
			(isUsingItem(cameraEntity, minecraft) || !cameraEntity.isFallFlying() && (isInteracting(cameraEntity, minecraft) ||
				isAttacking(minecraft) || isPicking(minecraft)));
	}
	
	private static boolean isUsingItem(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return cameraEntity.isUsingItem() && Config.CLIENT.getTurningModeWhenUsingItem().shouldTurn(minecraft.hitResult) &&
			!cameraEntity.getUseItem().isEdible();
	}
	
	private static boolean isInteracting(LivingEntity cameraEntity, Minecraft minecraft)
	{
		return minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem() &&
			Config.CLIENT.getTurningModeWhenInteracting().shouldTurn(minecraft.hitResult);
	}
	
	private static boolean isAttacking(Minecraft minecraft)
	{
		return minecraft.options.keyAttack.isDown() && Config.CLIENT.getTurningModeWhenAttacking().shouldTurn(minecraft.hitResult);
	}
	
	private static boolean isPicking(Minecraft minecraft)
	{
		return minecraft.options.keyPickItem.isDown() && Config.CLIENT.getTurningModeWhenPicking().shouldTurn(minecraft.hitResult);
	}
	
	public boolean shouldEntityFollowCamera(LivingEntity cameraEntity)
	{
		return (this.isAiming && !Config.CLIENT.getCrosshairType().isAimingDecoupled() || cameraEntity.isFallFlying()) ||
			!Config.CLIENT.isCameraDecoupled();
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, Entity entity)
	{
		if(entity instanceof LivingEntity living)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, living));
		}
		
		return false;
	}
	
	@Override
	public void changePerspective(Perspective perspective)
	{
		((OptionsDuck) Minecraft.getInstance().options).shouldersurfing$setCameraTypeDirect(perspective.getCameraType());
		this.setShoulderSurfing(Perspective.SHOULDER_SURFING.equals(perspective));
	}
	
	@Override
	public void togglePerspective()
	{
		Minecraft minecraft = Minecraft.getInstance();
		Perspective perspective = Perspective.current();
		Perspective next = perspective.next(Config.CLIENT.replaceDefaultPerspective(), Config.CLIENT.skipThirdPersonFront());
		boolean isFirstPerson = next.getCameraType().isFirstPerson();
		this.changePerspective(next);
		minecraft.levelRenderer.needsUpdate();
		
		if(perspective.getCameraType().isFirstPerson() != isFirstPerson)
		{
			minecraft.gameRenderer.checkEntityPostEffect(isFirstPerson ? minecraft.getCameraEntity() : null);
		}
		
		if(Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(next);
		}
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
	
	public void setShoulderSurfing(boolean isShoulderSurfing)
	{
		if(!this.isShoulderSurfing && isShoulderSurfing)
		{
			this.resetState();
		}
		
		this.isShoulderSurfing = isShoulderSurfing;
	}
	
	@Override
	public boolean isAiming()
	{
		return this.isAiming;
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
	
	public InputHandler getInputHandler()
	{
		return this.inputHandler;
	}
	
	@Override
	public void resetState()
	{
		this.camera.resetState();
		this.crosshairRenderer.resetState();
	}
	
	public static ShoulderSurfingImpl getInstance()
	{
		return (ShoulderSurfingImpl) ShoulderSurfing.getInstance();
	}
}
