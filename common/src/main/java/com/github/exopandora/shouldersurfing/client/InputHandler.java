package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.MathUtil;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class InputHandler
{
	public static final KeyBinding CAMERA_LEFT = createKeyMapping("adjust_camera_left", GLFW.GLFW_KEY_LEFT);
	public static final KeyBinding CAMERA_RIGHT = createKeyMapping("adjust_camera_right", GLFW.GLFW_KEY_RIGHT);
	public static final KeyBinding CAMERA_IN = createKeyMapping("adjust_camera_in", GLFW.GLFW_KEY_UP);
	public static final KeyBinding CAMERA_OUT = createKeyMapping("adjust_camera_out", GLFW.GLFW_KEY_DOWN);
	public static final KeyBinding CAMERA_UP = createKeyMapping("adjust_camera_up", GLFW.GLFW_KEY_PAGE_UP);
	public static final KeyBinding CAMERA_DOWN = createKeyMapping("adjust_camera_down", GLFW.GLFW_KEY_PAGE_DOWN);
	public static final KeyBinding SWAP_SHOULDER = createKeyMapping("swap_shoulder", GLFW.GLFW_KEY_O);
	public static final KeyBinding TOGGLE_FIRST_PERSON = createKeyMapping("toggle_first_person", InputMappings.UNKNOWN.getValue());
	public static final KeyBinding TOGGLE_THIRD_PERSON_FRONT = createKeyMapping("toggle_third_person_front", InputMappings.UNKNOWN.getValue());
	public static final KeyBinding TOGGLE_THIRD_PERSON_BACK = createKeyMapping("toggle_third_person_back", InputMappings.UNKNOWN.getValue());
	public static final KeyBinding FREE_LOOK = createKeyMapping("free_look", GLFW.GLFW_KEY_LEFT_ALT);
	public static final KeyBinding TOGGLE_CAMERA_COUPLING = createKeyMapping("toggle_camera_coupling", InputMappings.UNKNOWN.getValue());
	
	private final ShoulderSurfingImpl instance;
	
	public InputHandler(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
	}
	
	public void tick()
	{
		GameSettings options = Minecraft.getInstance().options;
		
		while(TOGGLE_FIRST_PERSON.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				this.instance.changePerspective(Perspective.FIRST_PERSON);
			}
			else
			{
				this.instance.changePerspective(Perspective.SHOULDER_SURFING);
			}
		}
		
		while(TOGGLE_THIRD_PERSON_FRONT.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				this.instance.changePerspective(Perspective.THIRD_PERSON_FRONT);
			}
			else
			{
				this.instance.changePerspective(Perspective.SHOULDER_SURFING);
			}
		}
		
		while(TOGGLE_THIRD_PERSON_BACK.consumeClick())
		{
			if(this.instance.isShoulderSurfing())
			{
				this.instance.changePerspective(Perspective.THIRD_PERSON_BACK);
			}
			else
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
		
		while(TOGGLE_CAMERA_COUPLING.consumeClick())
		{
			this.instance.toggleCameraCoupling();
		}
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
	
	private static @NotNull KeyBinding createKeyMapping(String key, int keyCode)
	{
		return new KeyBinding("key." + MOD_ID + "." + key, keyCode, "Shoulder Surfing");
	}
}
