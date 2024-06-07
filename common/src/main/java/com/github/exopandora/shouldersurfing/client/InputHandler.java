package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.MathUtil;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class InputHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding CAMERA_LEFT = new KeyBinding("Camera left", GLFW.GLFW_KEY_LEFT, KEY_CATEGORY);
	public static final KeyBinding CAMERA_RIGHT = new KeyBinding("Camera right", GLFW.GLFW_KEY_RIGHT, KEY_CATEGORY);
	public static final KeyBinding CAMERA_IN = new KeyBinding("Camera closer", GLFW.GLFW_KEY_UP, KEY_CATEGORY);
	public static final KeyBinding CAMERA_OUT = new KeyBinding("Camera farther", GLFW.GLFW_KEY_DOWN, KEY_CATEGORY);
	public static final KeyBinding CAMERA_UP = new KeyBinding("Camera up", GLFW.GLFW_KEY_PAGE_UP, KEY_CATEGORY);
	public static final KeyBinding CAMERA_DOWN = new KeyBinding("Camera down", GLFW.GLFW_KEY_PAGE_DOWN, KEY_CATEGORY);
	public static final KeyBinding SWAP_SHOULDER = new KeyBinding("Swap shoulder", GLFW.GLFW_KEY_O, KEY_CATEGORY);
	public static final KeyBinding TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", InputMappings.UNKNOWN.getValue(), KEY_CATEGORY);
	public static final KeyBinding FREE_LOOK = new KeyBinding("Free look", GLFW.GLFW_KEY_LEFT_ALT, KEY_CATEGORY);
	
	private final ShoulderSurfingImpl instance;
	
	public InputHandler(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
	}
	
	public void tick()
	{
		GameSettings options = Minecraft.getInstance().options;
		
		while(TOGGLE_SHOULDER_SURFING.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				this.instance.changePerspective(Perspective.FIRST_PERSON);
			}
			else if(options.getCameraType() == PointOfView.FIRST_PERSON)
			{
				this.instance.changePerspective(Perspective.SHOULDER_SURFING);
			}
		}
		
		while(CAMERA_LEFT.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraLeft();
			}
		}
		
		while(CAMERA_RIGHT.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraRight();
			}
		}
		
		while(CAMERA_OUT.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraOut();
			}
		}
		
		while(CAMERA_IN.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraIn();
			}
		}
		
		while(CAMERA_UP.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraUp();
			}
		}
		
		while(CAMERA_DOWN.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.adjustCameraDown();
			}
		}
		
		while(SWAP_SHOULDER.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				Config.CLIENT.swapShoulder();
			}
		}
		
		while(options.keyTogglePerspective.consumeClick())
		{
			this.instance.togglePerspective();
		}
		
		while(FREE_LOOK.consumeClick());
	}
	
	public void updateMovementInput(MovementInput input)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		Vec2f moveVector = new Vec2f(input.leftImpulse, input.forwardImpulse);
		
		if(this.instance.isShoulderSurfing() && this.instance.isFreeLooking())
		{
			moveVector.rotateDegrees(MathHelper.degreesDifference(cameraEntity.yRot, this.instance.getCamera().getFreeLookYRot()));
			input.leftImpulse = moveVector.x();
			input.forwardImpulse = moveVector.y();
		}
		else if(this.instance.isShoulderSurfing() && minecraft.player != null && cameraEntity == minecraft.player)
		{
			if(moveVector.lengthSquared() > 0)
			{
				ShoulderSurfingCamera camera = this.instance.getCamera();
				ClientPlayerEntity player = minecraft.player;
				float yRot = player.yRot;
				
				if(this.instance.isEntityRotationDecoupled(player, minecraft))
				{
					float cameraXRot = camera.getXRot();
					float cameraYRot = camera.getYRot();
					Vec2f rotated = moveVector.rotateDegrees(cameraYRot);
					float xRot = cameraXRot * 0.5F;
					float xRotO = player.xRot;
					float yRotO = yRot;
					yRot = (float) MathHelper.wrapDegrees(Math.atan2(-rotated.x(), rotated.y()) * MathUtil.RAD_TO_DEG);
					xRot = xRotO + MathHelper.degreesDifference(xRotO, xRot) * 0.25F;
					yRot = yRotO + MathHelper.degreesDifference(yRotO, yRot) * 0.25F;
					player.xRot = xRot;
					player.yRot = yRot;
				}
				
				moveVector = moveVector.rotateDegrees(MathHelper.degreesDifference(yRot, camera.getYRot()));
			}
			
			input.leftImpulse = moveVector.x();
			input.forwardImpulse = moveVector.y();
		}
	}
}
