package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo
{
	@Shadow
	protected abstract void move(double x, double y, double z);
	
	@Shadow
	protected abstract void setRotation(float yRot, float xRot);
	
	@Inject
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;setPosition(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0
		)
	)
	private void setupRotations(IBlockReader level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick, CallbackInfo ci)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity && ((LivingEntity) cameraEntity).isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vec2f rotations = camera.calcRotations(partialTick);
			this.setRotation(rotations.y(), rotations.x());
		}
	}
	
	@Redirect
	(
		method = "setup",
		at = @At
		(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;move(DDD)V",
			ordinal = 0
		)
	)
	private void setupPosition(ActiveRenderInfo cameraIn, double x, double y, double z, IBlockReader level, Entity cameraEntity, boolean detached, boolean isMirrored, float partialTick)
	{
		if(Perspective.SHOULDER_SURFING == Perspective.current() && !(cameraEntity instanceof LivingEntity && ((LivingEntity) cameraEntity).isSleeping()))
		{
			ShoulderSurfingCamera camera = ShoulderSurfingImpl.getInstance().getCamera();
			Vector3d cameraOffset = camera.calcOffset(cameraIn, level, partialTick, cameraEntity);
			this.move(-cameraOffset.z(), cameraOffset.y(), cameraOffset.x());
		}
		else
		{
			this.move(x, y, z);
		}
	}
}
