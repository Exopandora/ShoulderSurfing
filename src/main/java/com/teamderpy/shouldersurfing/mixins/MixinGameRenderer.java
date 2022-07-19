package com.teamderpy.shouldersurfing.mixins;

import java.util.function.Predicate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
	@Shadow
	private Camera mainCamera;

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
	private void onSetupCamera(float f, long l, PoseStack poseStack, CallbackInfo ci) {
		ShoulderSurfing.INSTANCE.getShoulderRenderer().offsetCamera(mainCamera, Minecraft.getInstance().level, f);
	}

	@Redirect
	(
		method = "pick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/projectile/ProjectileUtil.getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
		)
	)
	private EntityHitResult getEntityHitResult(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distanceSq)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(this.mainCamera, shooter, Minecraft.getInstance().getFrameTime(), distanceSq);
			return ProjectileUtil.getEntityHitResult(shooter, look.traceEndPos(), look.cameraPos(), boundingBox, filter, distanceSq);
		}
		
		return ProjectileUtil.getEntityHitResult(shooter, startVec, endVec, boundingBox, filter, distanceSq);
	}


}
