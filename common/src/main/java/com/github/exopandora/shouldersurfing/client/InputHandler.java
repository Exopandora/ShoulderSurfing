package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.lwjgl.glfw.GLFW;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class InputHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyMapping CAMERA_LEFT = new KeyMapping("key." + MOD_ID + ".adjust_camera_left", GLFW.GLFW_KEY_LEFT, KEY_CATEGORY);
	public static final KeyMapping CAMERA_RIGHT = new KeyMapping("key." + MOD_ID + ".adjust_camera_right", GLFW.GLFW_KEY_RIGHT, KEY_CATEGORY);
	public static final KeyMapping CAMERA_IN = new KeyMapping("key." + MOD_ID + ".adjust_camera_in", GLFW.GLFW_KEY_UP, KEY_CATEGORY);
	public static final KeyMapping CAMERA_OUT = new KeyMapping("key." + MOD_ID + ".adjust_camera_out", GLFW.GLFW_KEY_DOWN, KEY_CATEGORY);
	public static final KeyMapping CAMERA_UP = new KeyMapping("key." + MOD_ID + ".adjust_camera_up", GLFW.GLFW_KEY_PAGE_UP, KEY_CATEGORY);
	public static final KeyMapping CAMERA_DOWN = new KeyMapping("key." + MOD_ID + ".adjust_camera_down", GLFW.GLFW_KEY_PAGE_DOWN, KEY_CATEGORY);
	public static final KeyMapping SWAP_SHOULDER = new KeyMapping("key." + MOD_ID + ".swap_shoulder", GLFW.GLFW_KEY_O, KEY_CATEGORY);
	public static final KeyMapping TOGGLE_SHOULDER_SURFING = new KeyMapping("key." + MOD_ID + ".toggle_perspective", InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
	public static final KeyMapping FREE_LOOK = new KeyMapping("key." + MOD_ID + ".free_look", GLFW.GLFW_KEY_LEFT_ALT, KEY_CATEGORY);
	
	private final ShoulderSurfingImpl instance;
	
	public InputHandler(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
	}
	
	public void tick()
	{
		Options options = Minecraft.getInstance().options;
		
		while(TOGGLE_SHOULDER_SURFING.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				this.instance.changePerspective(Perspective.FIRST_PERSON);
			}
			else if(options.getCameraType() == CameraType.FIRST_PERSON)
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
	
	public void updateMovementInput(Input input)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Entity cameraEntity = minecraft.getCameraEntity();
		Vec2f moveVector = new Vec2f(input.leftImpulse, input.forwardImpulse);
		
		if(this.instance.isShoulderSurfing() && this.instance.isFreeLooking())
		{
			moveVector.rotateDegrees(Mth.degreesDifference(cameraEntity.getYRot(), this.instance.getCamera().getFreeLookYRot()));
			input.leftImpulse = moveVector.x();
			input.forwardImpulse = moveVector.y();
		}
		else if(this.instance.isShoulderSurfing() && minecraft.player != null && cameraEntity == minecraft.player)
		{
			if(moveVector.lengthSquared() > 0)
			{
				ShoulderSurfingCamera camera = this.instance.getCamera();
				LocalPlayer player = minecraft.player;
				float yRot = player.getYRot();
				
				if(this.instance.isEntityRotationDecoupled(player, minecraft))
				{
					float cameraXRot = camera.getXRot();
					float cameraYRot = camera.getYRot();
					Vec2f rotated = moveVector.rotateDegrees(cameraYRot);
					float xRot = cameraXRot * 0.5F;
					float xRotO = player.getXRot();
					float yRotO = yRot;
					yRot = (float) Mth.wrapDegrees(Math.atan2(-rotated.x(), rotated.y()) * Mth.RAD_TO_DEG);
					xRot = xRotO + Mth.degreesDifference(xRotO, xRot) * 0.25F;
					yRot = yRotO + Mth.degreesDifference(yRotO, yRot) * 0.25F;
					player.setXRot(xRot);
					player.setYRot(yRot);
				}
				
				moveVector = moveVector.rotateDegrees(Mth.degreesDifference(yRot, camera.getYRot()));
			}
			
			input.leftImpulse = moveVector.x();
			input.forwardImpulse = moveVector.y();
		}
	}
}
